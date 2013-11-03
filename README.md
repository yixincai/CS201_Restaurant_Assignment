##Restaurant Project Repository

###Student Information
  + Name: Yixin Cai
  + User Name: yixincai
  + USC Email: yixincai@usc.edu
  + USC ID: 9737754008
  + Lecture Section: TTh 11:00AM - 12:20PM
  + Lab Section: W 12:00PM - 1:50PM

###Instructions for Running
  + Import both src and images folder into a java project in Eclipse, also include JUnit 3 library. Click run to start the program.

###Design Document
  + [Design Document](DesignDoc.pdf)

###Interaction Diagram
  + [Interaction Diagram](InteractionDiagram.jpeg)

###Resources
  + [Restaurant v1](http://www-scf.usc.edu/~csci201/readings/restaurant-v1.html)
  + [Agent Roadmap](http://www-scf.usc.edu/~csci201/readings/agent-roadmap.html)

###Hacks for v2.1
  + Make customers with name of 5 or 7 or 10 or 20 to make them with money the same as their names
  + 5 - No money to order anything, may stay/leave (based on random generator, be patient to see two possibilities)
  + 5 - By making it hungry multiple times we can actually see its debt increasing
  + 7 - Enough money to order only one thing, will order the cheapest item
  + 7 will leave the reataurant the first time because cook has no salad
  + 7 will successfully get the salad the second time he gets hungry because now cook has food (hopefully, if the market has it)
  + 10 - Enough money to order from a selection of two or more items, may choose different items
  + 20 - Enough money to everything

###Hacks for v2.2
  + The cook will check his inventory first, and send an order list to the first market
  + The first market have 50% chance to fulfill the order - satisfying the requirement in scenario 1
  + The first market have 50% chance to fail to fulfill the order and the second market has 50% to send a bill cashier can pay - satisfying the requirement in scenario 2
  + The first market have 50% chance to fail to fulfill the order and the second market has 50% to send a bill cashier cannot pay - satisfying the non-normal scenario in extra credit