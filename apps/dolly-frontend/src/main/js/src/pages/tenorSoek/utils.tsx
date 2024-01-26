export const createOptions = (optionsList: Array<string>) => {
	if (!optionsList || optionsList.length === 0) {
		return []
	}
	return optionsList.map((option) => ({
		value: option,
		label: option,
	}))
}
