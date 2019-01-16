REPLACE INTO `role` VALUES (1,'ADMIN');
REPLACE INTO `role` VALUES (2,'MEDIC');
REPLACE INTO `role` VALUES (3,'NURSE');
REPLACE INTO `role` VALUES (4,'PATIENT');
REPLACE INTO `user` VALUES (0, 1, '0000', 'ZwrsFMzcMhA6kqCjlGdAFw==', 'ooznz5oyEl/Jkbiqnjjn5g==', 'ooznz5oyEl/Jkbiqnjjn5g==', '$2a$10$.EEmX/EniDQNmH21ZtW4KeGe3/mvPUK4qFNvct.Y/asBeptkb6JaG');
REPLACE INTO `user` VALUES (-1, 1, '0001', 'X+xrUtRrhBQmxUJz7IzNHGoKEkF7ls5V4STkBU9oUHM=', 'ZazbtmI/GUZlytVdTPbPXw==', '0IolieN6K+FKs/PW3ML2pQ==', '$2a$10$.EEmX/EniDQNmH21ZtW4KeGe3/mvPUK4qFNvct.Y/asBeptkb6JaG');
REPLACE INTO `user` VALUES (-2, 1, '0002', 'cS0VBbGSzh0wrfKI8PW+Eg==', 'CxXIPUqWkCFX8G4c7ChMhA==', 'V7lZuph4ROFZOKSPhp7O2w==', '$2a$10$.EEmX/EniDQNmH21ZtW4KeGe3/mvPUK4qFNvct.Y/asBeptkb6JaG');
REPLACE INTO `user` VALUES (-3, 1, '0003', '0wEQB+7gtNaEq85uCgJaRa3j9ivEeALAxkb0aLu17QU=', 'sl5YWY7PfBm1Ym5sAKijUA==', 'gKolzyFZchOBR3yi5HJHdA==', '$2a$10$.EEmX/EniDQNmH21ZtW4KeGe3/mvPUK4qFNvct.Y/asBeptkb6JaG');
REPLACE INTO `hospital` VALUES (-1, 'Lisboa', 'Portugal', 'Santa Maria');
REPLACE INTO `hospital` VALUES (-2, 'Evora', 'Portugal', 'Espirito Santo');
REPLACE INTO `user_hospital` VALUES (0,-2);
REPLACE INTO `user_hospital` VALUES (-1,-1);
REPLACE INTO `user_hospital` VALUES (-2,-2);
REPLACE INTO `user_hospital` VALUES (-3,-2);
REPLACE INTO `user_role` VALUES (1,0);
REPLACE INTO `user_role` VALUES (2,-1);
REPLACE INTO `user_role` VALUES (4,-2);
REPLACE INTO `user_role` VALUES (3,-3);
REPLACE INTO `record` VALUES (-1, 'VIjP8cW9D0WDBI2xQPKWrA==','8cfoXlHZZsk7MG3XOJpNcgHwOUNQFtugEfjZEEMklCTuPDiN3WSwsKgOUw/t6ukQ', 0, 0);
REPLACE INTO `record` VALUES (-2, 'jRjCKt63AQXsvH6BoQdo7g==','iH0Gpw6gpGwGLRB1YY4YRArzbToNGbKI99iVijoyA6WK2A97VwwfHwpxj8NhWZfo', 0, -1);
REPLACE INTO `record` VALUES (-3, 'I/RAavwCB3xpryOHB5wtWQ==','VXnEyW7zj6csjphhJESGyltH/kIE3kV9PnS4kA00Zi2v4Iq9QlWX35LiNPsGbyDe', 0, -2);
REPLACE INTO `record` VALUES (-4, 'hd9eyo75NY9y8GeUpsBsBw==','7QUYThKL8JOthoIyXBz7ZxkZbg1Gqwag9fN2A0tAORiTOCX5xR2eBjrCK4Xb4AZx', 0, -3);



