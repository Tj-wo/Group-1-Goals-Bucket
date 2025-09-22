@echo off
(
  for /r %%f in (*.java) do (
    echo -------------------- "%%f" --------------------
    type "%%f"
    echo.
  )
  for /r %%f in (*.xhtml) do (
    echo -------------------- "%%f" --------------------
    type "%%f"
    echo.
  )
  for /r %%f in (*.jsf) do (
    echo -------------------- "%%f" --------------------
    type "%%f"
    echo.
  )
  for /r %%f in (*.xml) do (
    echo -------------------- "%%f" --------------------
    type "%%f"
    echo.
  )
  for /r %%f in (*.properties) do (
    echo -------------------- "%%f" --------------------
    type "%%f"
    echo.
  )
  for /r %%f in (*.css) do (
    echo -------------------- "%%f" --------------------
    type "%%f"
    echo.
  )
  for /r %%f in (*.js) do (
    echo -------------------- "%%f" --------------------
    type "%%f"
    echo.
  )
) > all_code.txt

echo Done! Output saved to all_code.txt
pause
