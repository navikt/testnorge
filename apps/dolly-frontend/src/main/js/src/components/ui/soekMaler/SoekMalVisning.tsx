import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import * as _ from 'lodash-es'
import {
	arbeidInntektPaths,
	folkeregisteretPaths,
	pensjonPaths,
	skattPaths,
	virksomhetPaths,
} from '@/pages/tenorSoek/soekFormTabs/soekFormPaths'
import { arrayToString, codeToNorskLabel, formatDate, oversettBoolean } from '@/utils/DataFormatter'

const UGRUPPERT = 'annet'

const pathGroups: Record<string, string[]> = {
	...folkeregisteretPaths,
	...skattPaths,
	...arbeidInntektPaths,
	...pensjonPaths,
	...virksomhetPaths,
}

const pathToHeading: Record<string, string> = Object.entries(pathGroups).reduce(
	(acc, [heading, paths]) => {
		paths.forEach((path) => {
			acc[path] = heading
		})
		return acc
	},
	{} as Record<string, string>,
)

const headingOrder = [...Object.keys(pathGroups), UGRUPPERT]

const isObject = (value: unknown): value is Record<string, unknown> =>
	typeof value === 'object' && value !== null && !Array.isArray(value)

const isIsoDate = (value: string): boolean => /^\d{4}-\d{2}-\d{2}(T\d{2}:\d{2}:\d{2})?/.test(value)

const formatValue = (value: unknown): unknown => {
	if (typeof value === 'boolean') {
		return oversettBoolean(value)
	}
	if (Array.isArray(value)) {
		return arrayToString(value.map(formatValue))
	}
	if (typeof value === 'string') {
		if (isIsoDate(value)) {
			return formatDate(value)
		}
		return codeToNorskLabel(value)
	}
	return value
}

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

const groupKriterier = (soekKriterier: Record<string, unknown>) => {
	const grupper = _.groupBy(
		flattenKriterier(soekKriterier),
		(entry) => pathToHeading[entry.title] ?? UGRUPPERT,
	)
	return headingOrder
		.filter((heading) => grupper[heading]?.length)
		.map((heading) => ({
			heading,
			entries: grupper[heading].map((entry) => ({
				...entry,
				label: entry.title.startsWith(`${heading}.`)
					? entry.title.slice(heading.length + 1)
					: entry.title,
			})),
		}))
}

interface SoekMalVisningProps {
	soekKriterier: Record<string, unknown>
}

export const SoekMalVisning = ({ soekKriterier }: SoekMalVisningProps) => {
	return (
		<div className="bestilling-visning">
			{groupKriterier(soekKriterier).map(({ heading, entries }) => (
				<>
					<h4 style={{ marginTop: 0 }}>{codeToNorskLabel(heading)}</h4>
					<div key={heading} className="bestilling-blokk" style={{ marginBottom: '20px' }}>
						<div className="flexbox--flex-wrap">
							{entries.map(({ title, label, value }) => (
								<TitleValue
									key={title}
									title={codeToNorskLabel(label)}
									value={formatValue(value)}
								/>
							))}
						</div>
					</div>
				</>
			))}
		</div>
	)
}
