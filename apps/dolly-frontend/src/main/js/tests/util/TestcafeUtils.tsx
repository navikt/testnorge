import { Selector } from 'testcafe'

export const clickAllSiblings = async (controller: TestController, selector: Selector) => {
	const selectorCount = await selector.count

	for (let i = 0; i < selectorCount; i++) {
		await controller.click(selector.nth(i)).scrollBy(0, 50)
	}
}

export const scrollThroughPage = async (controller: TestController, timesToScroll: number) => {
	for (let i = 0; i < timesToScroll; i++) {
		await controller.scrollBy(0, 70).wait(50)
	}
}
