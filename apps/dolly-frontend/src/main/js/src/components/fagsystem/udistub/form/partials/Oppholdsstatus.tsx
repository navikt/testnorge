import React, { useState } from 'react'
import { DollySelect, FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { OppholdSammeVilkaar } from '@/components/fagsystem/udistub/form/partials/OppholdSammeVilkaar'
import { IkkeOppholdSammeVilkaar } from '@/components/fagsystem/udistub/form/partials/IkkeOppholdSammeVilkaar'
import { Option } from '@/service/SelectOptionsOppslag'
import { UseFormReturn } from 'react-hook-form/dist/types'

const basePath = 'udistub.oppholdStatus'
const pdlBasePath = 'pdldata.person.opphold'
const harOppholdsTillatelsePath = 'udistub.harOppholdsTillatelse'

const findInitialStatus = (formMethods: UseFormReturn) => {
	const oppholdsstatusObj = formMethods.getValues().udistub.oppholdStatus
	const eosEllerEFTAOpphold = Object.keys(oppholdsstatusObj).some((key) =>
		key.includes('eosEllerEFTA'),
	)
	if (eosEllerEFTAOpphold) {
		if (oppholdsstatusObj.eosEllerEFTABeslutningOmOppholdsrettPeriode) {
			return ['eosEllerEFTAOpphold', 'eosEllerEFTABeslutningOmOppholdsrett', '']
		}
		if (oppholdsstatusObj.eosEllerEFTAVedtakOmVarigOppholdsrettPeriode) {
			return ['eosEllerEFTAOpphold', 'eosEllerEFTAVedtakOmVarigOppholdsrett', '']
		}
		if (oppholdsstatusObj.eosEllerEFTAOppholdstillatelsePeriode) {
			return ['eosEllerEFTAOpphold', 'eosEllerEFTAOppholdstillatelse', '']
		}
	}
	if (oppholdsstatusObj.oppholdSammeVilkaar) {
		return ['tredjelandsBorgere', '', 'oppholdSammeVilkaar']
	}
	if (
		oppholdsstatusObj.ikkeOppholdSammeVilkaar ||
		formMethods.getValues().udistub.harOppholdsTillatelse === false
	) {
		return ['tredjelandsBorgere', '', 'ikkeOppholdSammeVilkaar']
	}
	if (oppholdsstatusObj.uavklart) {
		return ['tredjelandsBorgere', '', 'UAVKLART']
	}
	return ['', '', '']
}

function setPdlInitialValues(formMethods: UseFormReturn) {
	formMethods.setValue(`${pdlBasePath}`, [
		{
			type: 'OPPLYSNING_MANGLER',
			oppholdFra: null,
			oppholdTil: null,
		},
	])
}

export const Oppholdsstatus = ({ formMethods }: { formMethods: UseFormReturn }) => {
	const initialStatus = findInitialStatus(formMethods)
	const [oppholdsstatus, setOppholdsstatus] = useState(initialStatus[0])
	const [eosEllerEFTAtypeOpphold, setEosEllerEFTAtypeOpphold] = useState(initialStatus[1])
	const [tredjelandsBorgereValg, setTredjelandsBorgereValg] = useState(initialStatus[2])

	const endreOppholdsstatus = (value: string) => {
		setOppholdsstatus(value)
		setEosEllerEFTAtypeOpphold('')
		setTredjelandsBorgereValg('')
		formMethods.setValue(basePath, {})
		formMethods.clearErrors('udistub.oppholdStatus')
		setPdlInitialValues(formMethods)
	}

	const endreEosEllerEFTAtypeOpphold = (value: string) => {
		setEosEllerEFTAtypeOpphold(value)
		formMethods.setValue(basePath, {})
		formMethods.setValue(`udistub.oppholdStatus.${value}Periode`, {
			fra: null,
			til: null,
		})
		setPdlInitialValues(formMethods)
		formMethods.setValue(`udistub.oppholdStatus.${value}Effektuering`, null)
		formMethods.setValue(`udistub.oppholdStatus.${value}`, '')
	}

	const endreTredjelandsBorgereValg = (value: string) => {
		setTredjelandsBorgereValg(value)
		setPdlInitialValues(formMethods)
		formMethods.setValue(basePath, {})
		if (value === 'oppholdSammeVilkaar') {
			formMethods.setValue(harOppholdsTillatelsePath, true)
			formMethods.setValue('udistub.oppholdStatus.oppholdSammeVilkaar', {
				oppholdSammeVilkaarPeriode: { fra: null, til: null },
				oppholdSammeVilkaarEffektuering: null,
				oppholdstillatelseVedtaksDato: null,
				oppholdstillatelseType: '',
			})
		} else if (value === 'ikkeOppholdSammeVilkaar') {
			formMethods.setValue(basePath, {})
			formMethods.setValue(harOppholdsTillatelsePath, false)
			formMethods.setValue('udistub.oppholdStatus.ikkeOppholdstilatelseIkkeVilkaarIkkeVisum', {
				avslagEllerBortfall: {
					avgjorelsesDato: null,
				},
			})
		} else if (value === 'UAVKLART') {
			formMethods.setValue(basePath, { uavklart: true })
			formMethods.setValue(harOppholdsTillatelsePath, undefined)
		}
	}

	return (
		<div className="flexbox--flex-wrap">
			<DollySelect
				name="udistub.oppholdStatus"
				label="Innenfor eller utenfor EØS"
				value={oppholdsstatus}
				size="large"
				options={Options('oppholdsstatus')}
				onChange={(option: Option) => endreOppholdsstatus(option.value)}
				isClearable={false}
			/>
			{oppholdsstatus === 'eosEllerEFTAOpphold' && (
				<React.Fragment>
					<DollySelect
						name="eosEllerEFTAtypeOpphold"
						label="Type opphold"
						value={eosEllerEFTAtypeOpphold}
						options={Options('eosEllerEFTAtypeOpphold')}
						onChange={(option: Option) => endreEosEllerEFTAtypeOpphold(option.value)}
						size="xxlarge"
						isClearable={false}
					/>
					<FormDatepicker
						name={`udistub.oppholdStatus.${eosEllerEFTAtypeOpphold}Periode.fra`}
						duplicateName={`${pdlBasePath}[0].oppholdFra`}
						label="Oppholdstillatelse fra dato"
					/>
					<FormDatepicker
						name={`udistub.oppholdStatus.${eosEllerEFTAtypeOpphold}Periode.til`}
						duplicateName={`${pdlBasePath}[0].oppholdTil`}
						label="Oppholdstillatelse til dato"
					/>
					<FormDatepicker
						name={`udistub.oppholdStatus.${eosEllerEFTAtypeOpphold}Effektuering`}
						label="Effektueringsdato"
					/>
					{eosEllerEFTAtypeOpphold && (
						<FormSelect
							name={`udistub.oppholdStatus.${eosEllerEFTAtypeOpphold}`}
							label="Grunnlag for opphold"
							options={Options(eosEllerEFTAtypeOpphold)}
							isClearable={false}
							size="large"
						/>
					)}
				</React.Fragment>
			)}
			{oppholdsstatus === 'tredjelandsBorgere' && (
				<React.Fragment>
					<DollySelect
						name="tredjelandsBorgereValg"
						label="Status"
						value={tredjelandsBorgereValg}
						size="xxlarge"
						options={Options('tredjelandsBorgereValg')}
						onChange={(option: Option) => endreTredjelandsBorgereValg(option.value)}
						isClearable={false}
					/>
					{tredjelandsBorgereValg === 'oppholdSammeVilkaar' && (
						<OppholdSammeVilkaar formMethods={formMethods} />
					)}
					{tredjelandsBorgereValg === 'ikkeOppholdSammeVilkaar' && <IkkeOppholdSammeVilkaar />}
				</React.Fragment>
			)}
		</div>
	)
}
