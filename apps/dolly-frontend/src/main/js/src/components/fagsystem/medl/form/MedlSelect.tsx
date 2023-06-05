import * as React from 'react'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { MEDL_KILDER } from '@/components/fagsystem/medl/MedlConstants'

export type MedlSelectProps = {
	name: string
	label: string
	options?: any
	aktivKilde: MEDL_KILDER
	size?: string
	isClearable?: boolean
	afterChange?: () => void
	kodeverk?: string
}

export const MedlSelect = ({
	size,
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
		<FormikSelect
			label={label}
			afterChange={afterChange}
			size={size}
			options={options}
			kodeverk={kodeverk}
			name={name}
			{...props}
		/>
	) : null
}
