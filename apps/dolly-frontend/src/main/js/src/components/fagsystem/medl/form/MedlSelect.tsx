import * as React from 'react'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { MEDL_KILDER } from '@/components/fagsystem/medl/MedlConstants'
import { StylesConfig } from 'react-select/dist/declarations/src/styles'

export type MedlSelectProps = {
	name: string
	label: string
	options?: any
	aktivKilde: MEDL_KILDER
	size?: string
	style?: StylesConfig<any>
	isClearable?: boolean
	afterChange?: () => void
	kodeverk?: string
}

export const MedlSelect = ({
	size,
	style,
	name,
	label,
	afterChange,
	options,
	aktivKilde,
	kodeverk,
	...props
}: MedlSelectProps) => {
	const gosysMelosysFelter = ['grunnlag', 'dekning', 'lovvalgsland', 'lovvalg', 'kildedokument']
	const laanekassenFelter = [
		'studieinformasjon.delstudie',
		'studieinformasjon.soeknadInnvilget',
		'studieinformasjon.studieland',
		'studieinformasjon.statsborgerland',
	]
	const avgsysFelter = ['grunnlag', 'dekning']

	const hentAktiveFelter = (aktivKilde: MEDL_KILDER) => {
		switch (aktivKilde) {
			case MEDL_KILDER.SRVGOSYS:
			case MEDL_KILDER.SRVMELOSYS:
				return gosysMelosysFelter
			case MEDL_KILDER.LAANEKASSEN:
				return laanekassenFelter
			case MEDL_KILDER.AVGSYS:
				return avgsysFelter
		}
	}

	const aktiveFelter = hentAktiveFelter(aktivKilde)
	return aktiveFelter?.some((felt) => `medl.${felt}` === name) ? (
		<FormSelect
			label={label}
			afterChange={afterChange}
			size={size}
			options={options}
			kodeverk={kodeverk}
			name={name}
			styles={style}
			{...props}
		/>
	) : null
}
