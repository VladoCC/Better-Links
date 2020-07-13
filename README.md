# Better-then-Link
Simple, but yet effective app to share links, numbers, buisness cards and etc. using QR-codes. This app helps to share links fast, without needs to have Internet or other wireless conections. And you can use it in case you don't have links to person's social medias.

This project uses ZXing lib to create QR codes. All date stored in SQLite database. QR codes from this app can be scanned by any barcode scanners and contains easily readable JSON-file. Inner scanner analyses data automatically and send it through Intent system to apps that can handle this data installed in phone.
