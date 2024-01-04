-- Place your queries here. Docs available https://www.hugsql.org/

-- :name user-by-id :query :one
select * from user_info where user_id = :user-id

-- :name set-weight :execute
insert into user_info (user_id, weight)
values (:user-id, :weight)
on conflict (user_id) do update
set weight = EXCLUDED.weight;

-- :name set-height :execute
insert into user_info (user_id, height)
values (:user-id, :height)
on conflict (user_id) do update
set height = EXCLUDED.height;
