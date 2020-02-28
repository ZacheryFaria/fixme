# fixme

Fixme is stock exchange simulator.

It provides a GUI for both managing the Market (Inventory that processess transactions) as well as a GUI for the Broker (to place orders).

Network code is made with [netty.io](https://netty.io/) to provide robust, fast, asynchronous networking. Allowing for the Market and Router to process several transactions at a given moment.

There is a SQL datastore for both ongoing trasactions as well as fulfilled orders.

A modified version of the [FIX Protocol](https://en.wikipedia.org/wiki/Financial_Information_eXchange) has been implemented and adhered to for the transferring of order messages.
