import { useHentLagredeSoek } from '@/utils/hooks/useSoek'
import { Chips, VStack } from '@navikt/ds-react'
import * as _ from 'lodash-es'
import { codeToNorskLabel } from '@/utils/DataFormatter'

export enum soekType {
	dolly = 'DOLLY',
	tenor = 'TENOR',
}

export const SisteSoek = ({
	soekType,
	formValues,
	handleChange,
	handleChangeAdresse,
	handleChangeList,
}) => {
	const { lagredeSoek, loading, error } = useHentLagredeSoek(soekType)
	const lagredeSoekData = []
	lagredeSoek?.forEach((soek) => {
		Object.entries(soek?.soekVerdi)?.forEach((verdi) => {
			if (verdi[0] === 'registreRequest' && Array.isArray(verdi[1])) {
				verdi[1]?.forEach((item) => {
					if (!lagredeSoekData?.some((i) => i.value === item.value)) {
						lagredeSoekData.push(item)
					}
				})
			} else if (!lagredeSoekData?.some((item) => item.path === verdi[1]?.path)) {
				lagredeSoekData.push(verdi[1])
			}
		})
	})

	return (
		<VStack gap="3" style={{ marginBottom: '15px' }}>
			{/*<h4>SISTE SØK:</h4>*/}
			<Chips>
				{lagredeSoekData?.slice(0, 10).map((option, idx) => (
					<Chips.Toggle
						key={option.label}
						selected={
							option.path === 'registreRequest'
								? _.get(formValues, option.path)?.includes(option.value)
								: _.get(formValues, option.path) === option.value
						}
						onClick={() => {
							if (option.path === 'registreRequest') {
								console.log('option: ', option) //TODO - SLETT MEG
								const registreValues = _.get(formValues, option.path) || []
								handleChangeList(
									!registreValues?.includes(option.value)
										? [
												...registreValues,
												{ value: option.value, label: codeToNorskLabel(option.value) },
											]
										: registreValues?.filter((item) => item !== option.value),
									option.path,
								)
							} else if (option.path.includes('adresse') || option.path.includes('harDeltBosted')) {
								const pathArray = option.path?.split('.')
								handleChangeAdresse(
									_.get(formValues, option.path) !== option.value ? option.value : null,
									pathArray[pathArray.length - 1],
								)
							} else {
								handleChange(
									_.get(formValues, option.path) !== option.value ? option.value : null,
									option.path?.split('.')[1].trim(),
								)
							}
						}}
					>
						{option.label}
					</Chips.Toggle>
				))}
			</Chips>
		</VStack>
	)
}
