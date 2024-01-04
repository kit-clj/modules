-- Place your queries here. Docs available https://www.hugsql.org/

-- :name user-by-id :query :one
select * from user_info where user_id = :user-id

-- :name upsert-user :execute
insert into user_info (user_id, height, weight)
values (:user-id, :height, :weight)
on conflict (user_id) do update
set height = EXCLUDED.height,
weight = EXCLUDED.weight;