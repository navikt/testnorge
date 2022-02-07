import React, { useState } from 'react'
import { DollySelect, FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { OppholdSammeVilkaar } from '~/components/fagsystem/udistub/form/partials/OppholdSammeVilkaar'
import { IkkeOppholdSammeVilkaar } from '~/components/fagsystem/udistub/form/partials/IkkeOppholdSammeVilkaar'
import { Option } from '~/service/SelectOptionsOppslag'
import { FormikProps } from 'formik'

const basePath = 'udistub.oppholdStatus'
const pdlBasePath = 'pdldata.person.opphold'

const findInitialStatus = (formikBag: FormikProps<any>) => {
	const oppholdsstatusObj = formikBag.values.udistub.oppholdStatus
	const eosEllerEFTAOpphold = Object.keys(oppholdsstatusObj).some((key) =>
		key.includes('eosEllerEFTA')
	)
	if (eosEllerEFTAOpphold) {
		if (oppholdsstatusObj.eosEllerEFTABeslutningOmOppholdsrettPeriode)
			return ['eosEllerEFTAOpphold', 'eosEllerEFTABeslutningOmOppholdsrett', '']
		if (oppholdsstatusObj.eosEllerEFTAVedtakOmVarigOppholdsrettPeriode)
			return ['eosEllerEFTAOpphold', 'eosEllerEFTAVedtakOmVarigOppholdsrett', '']
		if (oppholdsstatusObj.eosEllerEFTAOppholdstillatelsePeriode)
			return ['eosEllerEFTAOpphold', 'eosEllerEFTAOppholdstillatelse', '']
	}
	if (oppholdsstatusObj.oppholdSammeVilkaar)
		return ['tredjelandsBorgere', '', 'oppholdSammeVilkaar']
	if (
		oppholdsstatusObj.ikkeOppholdSammeVilkaar ||
		formikBag.values.udistub.harOppholdsTillatelse === false
	)
		return ['tredjelandsBorgere', '', 'ikkeOppholdSammeVilkaar']
	if (oppholdsstatusObj.uavklart) return ['tredjelandsBorgere', '', 'UAVKLART']
	return ['', '', '']
}

function setPdlInitialValues(formikBag: FormikProps<any>) {
	formikBag.setFieldValue(`${pdlBasePath}`, [
		{
			type: 'OPPLYSNING_MANGLER',
			oppholdFra: null,
			oppholdTil: null,
		},
	])
}

export const Oppholdsstatus = ({ formikBag }: { formikBag: FormikProps<any> }) => {
	const initialStatus = findInitialStatus(formikBag)
	const [oppholdsstatus, setOppholdsstatus] = useState(initialStatus[0])
	const [eosEllerEFTAtypeOpphold, setEosEllerEFTAtypeOpphold] = useState(initialStatus[1])
	const [tredjelandsBorgereValg, setTredjelandsBorgereValg] = useState(initialStatus[2])

	const endreOppholdsstatus = (value: string) => {
		setOppholdsstatus(value)
		setEosEllerEFTAtypeOpphold('')
		setTredjelandsBorgereValg('')
		formikBag.setFieldValue(basePath, {})
		setPdlInitialValues(formikBag)
	}

	const endreEosEllerEFTAtypeOpphold = (value: string) => {
		setEosEllerEFTAtypeOpphold(value)
		formikBag.setFieldValue(basePath, {})
		formikBag.setFieldValue(`udistub.oppholdStatus.${value}Periode`, {
			fra: null,
			til: nul,
		})
		setPdlInitialValues(formikBag)
		formikBag.setFieldValue(`udistub.oppholdStatus.${value}Effektuering`, null)
		formikBag.setFieldValue(`udistub.oppholdStatus.${value}`, '')
	}

	const endreTredjelandsBorgereValg = (value: string) => {
		setTredjelandsBorgereValg(value)
		setPdlInitialValues(formikBag)
		formikBag.setFieldValue(basePath, {})
		if (value === 'oppholdSammeVilkaar') {
			formikBag.setFieldValue('udistub.harOppholdsTillatelse', true)
			formikBag.setFieldValue('udistub.oppholdStatus.oppholdSammeVilkaar', {
				oppholdSammeVilkaarPeriode: { fra: null, til: null },
				oppholdSammeVilkaarEffektuering: null,
				oppholdstillatelseVedtaksDato: null,
				oppholdstillatelseType: '',
			})
		} else if (value === 'ikkeOppholdSammeVilkaar') {
			formikBag.setFieldValue(basePath, {})
			formikBag.setFieldValue('udistub.harOppholdsTillatelse', false)
			formikBag.setFieldValue('udistub.oppholdStatus.ikkeOppholdstilatelseIkkeVilkaarIkkeVisum', {
				avslagEllerBortfall: {
					avgjorelsesDato: null,
				},
			})
		} else if (value === 'UAVKLART') {
			formikBag.setFieldValue(basePath, { uavklart: true })
			formikBag.setFieldValue('udistub.harOppholdsTillatelse', undefined)
		}
	}

	const feilmelding = (felt: string) => {
		if (!felt) {
			return { feilmelding: 'Feltet er påkrevd' }
		}
	}

	return (
		<React.Fragment>
			<DollySelect
				name="oppholdsstatus"
				label="Innenfor eller utenfor EØS"
				value={oppholdsstatus}
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
							formikBag.setFieldValue(`${pdlBasePath}[0].oppholdFra`, dato)
						}
						label="Oppholdstillatelse fra dato"
					/>
					<FormikDatepicker
						name={`udistub.oppholdStatus.${eosEllerEFTAtypeOpphold}Periode.til`}
						afterChange={(dato: Date) =>
							formikBag.setFieldValue(`${pdlBasePath}[0].oppholdTil`, dato)
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
						<OppholdSammeVilkaar formikBag={formikBag} />
					)}
					{tredjelandsBorgereValg === 'ikkeOppholdSammeVilkaar' && (
						<IkkeOppholdSammeVilkaar formikBag={formikBag} />
					)}
				</React.Fragment>
			)}
		</React.Fragment>
	)
}
