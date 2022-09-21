import { Selector } from 'testcafe'

export const clickAllSiblings = async (controller: TestController, selector: Selector) => {
	const selectorCount = await selector.count

	for (let i = 0; i < selectorCount; i++) {
		controller.click(selector.nth(i)).scrollBy(0, 50)
	}
}
