package com.petmily.repository.dto

data class Shop(
    var itemId: Long = 0L,
    var itemType: String = "",
    var itemName: String = "",
    var itemImg: String = "",
    var itemColor: String = "",
    var itemRarity: String = "",
)

/*
  "itemId": 8,
  "itemType": "background",
  "itemName": "Background 2",
  "itemImg": "bg2.jpg",
  "itemColor": "",
  "itemRarity": "A",
 */
