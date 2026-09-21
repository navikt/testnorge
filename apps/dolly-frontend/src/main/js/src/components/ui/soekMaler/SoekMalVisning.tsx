import { TitleValue } from '@/components/ui/titleValue/TitleValue'

const isObject = (value: unknown): value is Record<string, unknown> =>
	typeof value === 'object' && value !== null

const flattenKriterier = (
	kriterier: unknown,
	parentTitle = '',
): { title: string; value: unknown }[] => {
	if (!isObject(kriterier)) {
		return [{ title: parentTitle, value: kriterier }]
	}

	return Object.entries(kriterier).flatMap(([key, value]) => {
		const title = parentTitle ? `${parentTitle}.${key}` : key
		if (isObject(value)) {
			return flattenKriterier(value, title)
		}
		return [{ title, value }]
	})
}

interface SoekMalVisningProps {
	soekKriterier: Record<string, unknown>
}

// TODO: Gjoer visning penere
export const SoekMalVisning = ({ soekKriterier }: SoekMalVisningProps) => {
	return (
		<div className="bestilling-visning">
			<div className="bestilling-blokk">
				{flattenKriterier(soekKriterier).map(({ title, value }) => (
					<TitleValue key={title} title={title} value={value} />
				))}
			</div>
		</div>
	)
}
