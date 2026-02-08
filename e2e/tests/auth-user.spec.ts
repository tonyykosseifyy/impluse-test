import { expect, test } from "@playwright/test";

test("user login with wrong email first, then success and logout", async ({ page }) => {
  await page.goto("/login");

  await page.getByLabel("Email").fill("wrong-user@inpulse.dev");
  await page.getByLabel("Password").fill("ChangeMe123!");
  await page.getByRole("button", { name: "Sign in" }).click();

  await expect(page.getByText("Invalid email or password")).toBeVisible();

  await page.getByLabel("Email").fill("user@inpulse.dev");
  await page.getByRole("button", { name: "Sign in" }).click();

  await expect(page).toHaveURL(/\/dashboard$/);
  await expect(page.getByText("Authenticated Area")).toBeVisible();

  await page.getByRole("button", { name: "Logout" }).click();
  await expect(page).toHaveURL(/\/login$/);
});
