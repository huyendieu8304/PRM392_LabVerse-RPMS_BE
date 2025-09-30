-- INSERT INTO role (name)
-- SELECT 'PI'
-- WHERE NOT EXISTS (
--     SELECT 1 FROM role WHERE name = 'PI'
-- );
--
-- INSERT INTO role (name)
-- SELECT 'RESEARCHER'
-- WHERE NOT EXISTS (
--     SELECT 1 FROM role WHERE name = 'RESEARCHER'
-- );
--
-- INSERT INTO role (name)
-- SELECT 'INTERN'
-- WHERE NOT EXISTS (
--     SELECT 1 FROM role WHERE name = 'INTERN'
-- );


INSERT INTO prm392_labverse.dbo.role (id, name) VALUES (1, N'PI');
INSERT INTO prm392_labverse.dbo.role (id, name) VALUES (2, N'RESEARCHER');
INSERT INTO prm392_labverse.dbo.role (id, name) VALUES (3, N'INTERN');
