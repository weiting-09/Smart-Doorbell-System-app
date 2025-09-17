name: 功能需求 (Feature Request)
description: 提出新功能或改善建議
title: "[Feature] "
labels: [enhancement]
body:
- type: textarea
  id: description
  attributes:
  label: 功能描述
  description: 請簡要說明你希望新增或改善的功能
  placeholder: 例如：希望新增夜間模式
  validations:
  required: true
- type: textarea
  id: motivation
  attributes:
  label: 動機與背景
  description: 請說明這個功能對你的幫助或解決了什麼問題
  placeholder: 例如：夜間模式可以減少夜間用眼疲勞
- type: textarea
  id: solution
  attributes:
  label: 你期望的解決方案
  description: 如果有具體想法，請描述你期望的功能實現方式
- type: textarea
  id: other
  attributes:
  label: 其他補充
  description: 其他相關資訊