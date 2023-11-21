import React, { useState } from 'react'
import { DollySelect, FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { OppholdSammeVilkaar } from '@/components/fagsystem/udistub/form/partials/OppholdSammeVilkaar'
import { IkkeOppholdSammeVilkaar } from '@/components/fagsystem/udistub/form/partials/IkkeOppholdSammeVilkaar'
import { Option } from '@/service/SelectOptionsOppslag'
import { fixTimezone } from '@/components/ui/form/formUtils'
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

	const feilmelding = (felt: string) => {
		if (!felt) {
			return { feilmelding: 'Feltet er påkrevd' }
		}
	}

	return (
		<div className="flexbox--flex-wrap">
			<DollySelect
				name="oppholdsstatus"
				label="Innenfor eller utenfor EØS"
				value={oppholdsstatus}
				size="large"
				options={Options('oppholdsstatus')}
				onChange={(option: Option) => endreOppholdsstatus(option.value)}
				feil={feilmelding(oppholdsstatus)}
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
						feil={feilmelding(eosEllerEFTAtypeOpphold)}
						isClearable={false}
					/>
					<FormikDatepicker
						name={`udistub.oppholdStatus.${eosEllerEFTAtypeOpphold}Periode.fra`}
						afterChange={(dato: Date) =>
							formMethods.setValue(`${pdlBasePath}[0].oppholdFra`, fixTimezone(dato))
						}
						label="Oppholdstillatelse fra dato"
					/>
					<FormikDatepicker
						name={`udistub.oppholdStatus.${eosEllerEFTAtypeOpphold}Periode.til`}
						afterChange={(dato: Date) =>
							formMethods.setValue(`${pdlBasePath}[0].oppholdTil`, fixTimezone(dato))
						}
						label="Oppholdstillatelse til dato"
					/>
					<FormikDatepicker
						name={`udistub.oppholdStatus.${eosEllerEFTAtypeOpphold}Effektuering`}
						label="Effektueringsdato"
					/>
					{eosEllerEFTAtypeOpphold && (
						<FormikSelect
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
						feil={feilmelding(tredjelandsBorgereValg)}
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
