import React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { Alder } from '~/components/fagsystem/tpsf/form/personinformasjon/partials/alder/Alder'
import { Diskresjonskoder } from '~/components/fagsystem/tpsf/form/personinformasjon/partials/diskresjonskoder/Diskresjonskoder'
import Formatters from '~/utils/DataFormatter'

const initialValues = {
	identtype: 'FNR',
	kjonn: '',
	barnType: '',
	partnerNr: null,
	borHos: '',
	erAdoptert: false,
	alder: Formatters.randomIntInRange(1, 18),
	spesreg: '',
	utenFastBopel: false
}

export const Barn = ({ formikBag }) => {
	const getOptionsPartnerNr = () => {
		const partnere = formikBag.values.tpsf.relasjoner.partnere
		const options = []
		if (partnere)
			for (let i = 1; i <= partnere.length; i++) {
				options.push({ value: i, label: `Partner ${i}` })
			}
		return options
	}

	const optionsPartnerNr = getOptionsPartnerNr()

	return (
		<DollyFieldArray name="tpsf.relasjoner.barn" title="Barn" newEntry={initialValues}>
			{(path, idx) => (
				<React.Fragment key={idx}>
					<FormikSelect
						name={`${path}.identtype`}
						label="Identtype"
						options={Options('identtype')}
						isClearable={false}
					/>
					<FormikSelect name={`${path}.kjonn`} label="Kjønn" options={Options('kjonnBarn')} />
					<FormikSelect
						name={`${path}.barnType`}
						label="Foreldre"
						options={Options('barnType')}
						isClearable={false}
					/>
					{formikBag.values.tpsf.relasjoner.barn[idx].barnType === 'DITT' &&
						formikBag.values.tpsf.relasjoner.partnere && (
							<FormikSelect
								name={`${path}.partnerNr`}
								label="Forelder"
								options={optionsPartnerNr}
							/>
						)}
					{formikBag.values.tpsf.relasjoner.barn[idx].barnType === 'FELLES' &&
						formikBag.values.tpsf.relasjoner.partnere && (
							<FormikSelect
								name={`${path}.partnerNr`}
								label="Forelder 2"
								options={optionsPartnerNr}
							/>
						)}
					<FormikSelect
						name={`${path}.borHos`}
						label="Bor hos"
						options={Options('barnBorHos')}
						isClearable={false}
					/>
					<FormikCheckbox name={`${path}.erAdoptert`} label="Er adoptert" />
					<Alder basePath={path} formikBag={formikBag} />
					<Diskresjonskoder basePath={path} formikBag={formikBag} />
				</React.Fragment>
			)}
		</DollyFieldArray>
	)
}
