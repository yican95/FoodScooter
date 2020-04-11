DROP TABLE IF EXISTS Users CASCADE;
DROP TABLE IF EXISTS DeliveryRiders CASCADE;
DROP TABLE IF EXISTS FTRiders CASCADE;
DROP TABLE IF EXISTS FTSchedules CASCADE;
DROP TABLE IF EXISTS PTRiders CASCADE;
DROP TABLE IF EXISTS PTSchedules CASCADE;
DROP TABLE IF EXISTS PTShifts CASCADE;
DROP TABLE IF EXISTS Customers CASCADE;
DROP TABLE IF EXISTS FDSManagers CASCADE;
DROP TABLE IF EXISTS Promotions CASCADE;
DROP TABLE IF EXISTS Restaurants CASCADE;
DROP TABLE IF EXISTS RestaurantStaff CASCADE;
DROP TABLE IF EXISTS FoodItems CASCADE;
DROP TABLE IF EXISTS Orders CASCADE;
DROP TABLE IF EXISTS Reviews CASCADE;

DROP TRIGGER IF EXISTS addSpecificUserTrigger ON Users;
DROP TRIGGER IF EXISTS hashPasswordTrigger ON Users;
DROP TRIGGER IF EXISTS checkAcceptedOrdersTrigger ON Orders CASCADE;

CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE Users (
    uid INTEGER PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL
);

CREATE TABLE DeliveryRiders (
    drid INTEGER PRIMARY KEY,
    salary MONEY,
    rating INTEGER, /* NEW */
    FOREIGN KEY (drid) REFERENCES Users (uid) ON DELETE CASCADE
);

CREATE TABLE FTRiders (
    drid INTEGER PRIMARY KEY,
    dayOption INTEGER check(dayOption in (1,2,3,4,5,6,7)),
	shiftOption INTEGER check(shiftOption in (1,2,3,4)),
    FOREIGN KEY (drid) REFERENCES DeliveryRiders (drid) ON DELETE CASCADE
);

/*
CREATE TABLE PTSchedules (
    psid INTEGER PRIMARY KEY,
    totalHours INTEGER
);
*/

/* psid = id of assigned schedule */
CREATE TABLE PTRiders (
    drid INTEGER PRIMARY KEY,
    psid INTEGER,
    FOREIGN KEY (drid) REFERENCES DeliveryRiders (drid) ON DELETE CASCADE
--     FOREIGN KEY (psid) REFERENCES PTSchedules
);

CREATE TABLE PTShifts (
    ptsid INTEGER PRIMARY KEY,
    drid INTEGER,
    dow INTEGER check(dow in (1,2,3,4,5,6,7)),
    startHour INTEGER check(startHour in (10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21)),
    endHour INTEGER check(endHour in (11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22)),
	FOREIGN KEY (drid) REFERENCES PTRiders (drid) ON DELETE CASCADE,
	CHECK (startHour <= endHour)
);

CREATE TABLE Customers (
    cid INTEGER PRIMARY KEY,
    creditCardNumber VARCHAR(16),
    rewardPoints INTEGER,
    recentPlaces VARCHAR(100) ARRAY,
    FOREIGN KEY (cid) REFERENCES Users (uid) ON DELETE CASCADE
);

CREATE TABLE FDSManagers (
    fmid INTEGER PRIMARY KEY,
    FOREIGN KEY (fmid) REFERENCES Users (uid) ON DELETE CASCADE
);

CREATE TABLE Promotions (
    pid INTEGER PRIMARY KEY,
    startDate TIMESTAMP,
    endDate TIMESTAMP,
    promotionType VARCHAR(100),
    discount NUMERIC
);

/* pid = id of promotion offered by restaurant */
CREATE TABLE Restaurants (
    rid INTEGER PRIMARY KEY,
    name VARCHAR(100),
    description VARCHAR(1000),
    minimumPurchase MONEY,
    pid INTEGER,
    FOREIGN KEY (pid) REFERENCES Promotions (pid)
);

CREATE TABLE RestaurantStaff (
    rsid INTEGER PRIMARY KEY,
    employedBy INTEGER NOT NULL,
    FOREIGN KEY (rsid) REFERENCES Users (uid) ON DELETE CASCADE,
    FOREIGN KEY (employedBy) REFERENCES Restaurants (rid) ON DELETE CASCADE
);

CREATE TABLE FoodItems (
    fid INTEGER,
    rid INTEGER,
    name VARCHAR(100),
    category VARCHAR(100),
    price MONEY,
    dailyLimit INTEGER,
    PRIMARY KEY (fid, rid),
    FOREIGN KEY (rid) REFERENCES Restaurants (rid) ON DELETE CASCADE
);

CREATE TABLE Orders (
    oid INTEGER PRIMARY KEY,
    cid INTEGER,
    drid INTEGER,
    rid INTEGER,
    foodCost MONEY,
    deliveryFee MONEY,
    rewardPointsUsed INTEGER,
    paymentType VARCHAR(100),
    deliveryLocation VARCHAR(100),
    orderTime TIMESTAMP,
    departureTime TIMESTAMP,
    restaurantArrivalTime TIMESTAMP,
    restaurantDepartureTime TIMESTAMP,
    deliveryTime TIMESTAMP,
    FOREIGN KEY (cid) REFERENCES Customers (cid),
    FOREIGN KEY (drid) REFERENCES DeliveryRiders (drid),
    FOREIGN KEY (rid) REFERENCES Restaurants (rid)
);

CREATE TABLE Reviews (
    rvid INTEGER PRIMARY KEY,
    rid INTEGER,
    oid INTEGER,
    content VARCHAR(1000),
    FOREIGN KEY (rid) REFERENCES Restaurants (rid),
    FOREIGN KEY (oid) REFERENCES Orders (oid)
);

CREATE OR REPLACE FUNCTION addSpecificUser() RETURNS TRIGGER AS $$
    BEGIN
        CASE NEW.type
            WHEN 'Customer' THEN
                INSERT INTO Customers
                VALUES(NEW.uid);
            WHEN 'Delivery Rider' THEN
                INSERT INTO DeliveryRiders
                VALUES(NEW.uid);
            ELSE

        END CASE;
        RETURN NEW;
    END;
$$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION hashPassword() RETURNS TRIGGER AS $$
    BEGIN
        NEW.password = digest(NEW.password, 'sha1');
		RETURN NEW;
    END;
$$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION checkAcceptedOrders() RETURNS TRIGGER AS $$
	DECLARE
		aoid INTEGER;
    BEGIN
		SELECT O.oid INTO aoid
			FROM Orders O
			WHERE O.drid = NEW.drid AND O.deliveryTime IS NULL AND O.oid <> NEW.oid;
        IF aoid IS NOT NULL THEN 
			RAISE exception '% has already accepted %', NEW.drid, aoid;
		END IF
		RETURN NULL;
    END;
$$ LANGUAGE PLPGSQL;

CREATE TRIGGER addSpecificUserTrigger
    AFTER INSERT
    ON Users
    FOR EACH ROW
    EXECUTE FUNCTION addSpecificUser();
	
CREATE TRIGGER hashPasswordTrigger
    BEFORE UPDATE OR INSERT 
	ON Users
    FOR EACH ROW 
	EXECUTE FUNCTION hashPassword();

CREATE TRIGGER checkAcceptedOrdersTrigger
    AFTER UPDATE OF drid OR INSERT 
	ON Orders
	DEFERRABLE INITIALLY DEFERRED
    FOR EACH ROW 
	EXECUTE FUNCTION checkAcceptedOrders();


INSERT INTO Restaurants
VALUES (1, 'Ikea', 'A Swedish furniture company that is also known for their meatballs.', 10),
       (2, 'Seoul Good', 'A Korean fusion cafe by Punggol Promenade', 8),
       (3, 'Mr Chicken Rice', 'An up and coming restaurant that reinvented the Chicken Rice', 4);

INSERT INTO FoodItems
VALUES (1, 1, 'Swedish Meatballs', 'Swedish', 5, 100),
       (2, 1, 'Fried Rice', 'Local', 3, 100),
       (3, 1, 'Mushroom Soup', 'Western', 6, 100),
       (4, 1, 'Dumplings', 'Asian', 2, 150),

       (1, 2, 'Grilled Chicken Rice', 'Korean', 9.80, 100),
       (2, 2, 'Grilled Salmon Rice', 'Korean', 11.80, 100),
       (3, 2, 'Army Stew', 'Korean', 23.90, 50),
       (4, 2, 'Squid Ink Pasta', 'Western', 11.90, 100),

       (1, 3, 'Regular Chicken Rice', 'Singaporean', 3, 500),
       (2, 3, 'Cabbage with Sesame Oil', 'Singaporean', 3, 500),
       (3, 3, 'Char Siew', 'Singaporean', 3, 500);

INSERT INTO Users
VALUES (1, 'customer', 'customer', 'Customer'),
       (2, 'rider', 'rider', 'Delivery Rider');

-- INSERT INTO Orders
-- VALUES (1, 1, 2, 2, 100, 10, 'Credit Card', 'Seoul Good',
--         '2020-04-01 04:05:06', '2020-04-01 04:05:06', '2020-04-01 04:05:06', '2020-04-01 04:05:06', '2020-04-01 04:05:06'),
--        (2, 1, 2, 2, 100, 10, 'Credit Card', 'Seoul Good',
--         '2020-04-01 04:05:06', '2020-04-01 04:05:06', '2020-04-01 04:05:06', '2020-04-01 04:05:06', '2020-04-01 04:05:06'),
--        (3, 1, 2, 2, 100, 10, 'Credit Card', 'Seoul Good',
--         '2020-04-01 04:05:06', '2020-04-01 04:05:06', '2020-04-01 04:05:06', '2020-04-01 04:05:06', '2020-04-01 04:05:06'),
--        (4, 1, 2, 2, 100, 10, 'Credit Card', 'Seoul Good',
--         '2020-04-01 04:05:06', '2020-04-01 04:05:06', '2020-04-01 04:05:06', '2020-04-01 04:05:06', '2020-04-01 04:05:06');
