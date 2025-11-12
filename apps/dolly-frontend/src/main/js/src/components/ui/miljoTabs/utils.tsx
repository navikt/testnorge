export const mergeMiljoData = (data: any[], bestilteMiljoer: string[]) => {
	const mergeMiljo: any[] = []
	data?.forEach((item) => {
		const indexOfMiljo = mergeMiljo.findIndex((i) => i?.miljo === item?.miljo)
		if (indexOfMiljo >= 0) {
			mergeMiljo[indexOfMiljo].data?.push(item.data)
		} else {
			mergeMiljo.push({
				data: [item.data],
				miljo: item.miljo,
			})
		}
	})
	bestilteMiljoer?.forEach((miljo) => {
		if (!mergeMiljo?.find((i) => i?.miljo === miljo)) {
			mergeMiljo.push({ data: null, miljo: miljo })
		}
	})
	return mergeMiljo
}
