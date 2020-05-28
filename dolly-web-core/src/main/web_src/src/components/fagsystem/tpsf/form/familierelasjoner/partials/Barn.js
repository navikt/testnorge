import React from 'react'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { Alder } from '~/components/fagsystem/tpsf/form/personinformasjon/partials/alder/Alder'
import { Diskresjonskoder } from '~/components/fagsystem/tpsf/form/personinformasjon/partials/diskresjonskoder/Diskresjonskoder'
import Formatters from '~/utils/DataFormatter'
import useBoolean from '~/utils/hooks/useBoolean'

const initialValues = {
	identtype: 'FNR',
	kjonn: '',
	barnType: '',
	partnerNr: null,
	borHos: '',
	erAdoptert: false,
	alder: Formatters.randomIntInRange(0, 17),
	spesreg: '',
	utenFastBopel: false,
	statsborgerskap: '',
	statsborgerskapRegdato: ''
}

const initialValuesDoedfoedt = {
	identtype: 'FDAT',
	kjonn: '',
	barnType: '',
	partnerNr: null,
	foedselsdato: '',
	doedsdato: ''
}

export const Barn = ({ formikBag }) => {
	const [isDoedfoedt, setDoedfoedtTrue, setDoedfoedtFalse] = useBoolean(false)
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

	initialValues.alder = Formatters.randomIntInRange(0, 17)

	const handleIdenttypeChange = (path, ident) => {
		if (ident.value === 'FDAT') {
			setDoedfoedtTrue()
			formikBag.setFieldValue(`${path}`, initialValuesDoedfoedt)
		} else {
			setDoedfoedtFalse()
			formikBag.setFieldValue(`${path}`, initialValues)
			formikBag.setFieldValue(`${path}.identtype`, ident.value)
		}
	}

	const handleFoedselsdatoChange = (path, dato) => {
		formikBag.setFieldValue(`${path}.foedselsdato`, dato)
		formikBag.setFieldValue(`${path}.doedsdato`, dato)
	}

	return (
		<FormikDollyFieldArray name="tpsf.relasjoner.barn" header="Barn" newEntry={initialValues}>
			{(path, idx) => (
				<React.Fragment key={idx}>
					<FormikSelect
						name={`${path}.identtype`}
						label="Identtype"
						options={Options('identtypeBarn')}
						onChange={ident => handleIdenttypeChange(path, ident)}
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
					{!isDoedfoedt && (
						<FormikSelect
							name={`${path}.borHos`}
							label="Bor hos"
							options={Options('barnBorHos')}
							isClearable={false}
						/>
					)}
					{!isDoedfoedt && (
						<FormikCheckbox name={`${path}.erAdoptert`} label="Er adoptert" checkboxMargin />
					)}
					{!isDoedfoedt && (
						<FormikSelect
							name={`${path}.statsborgerskap`}
							label="Statsborgerskap"
							kodeverk={AdresseKodeverk.StatsborgerskapLand}
						/>
					)}
					{!isDoedfoedt && (
						<FormikDatepicker name={`${path}.statsborgerskapRegdato`} label="Statsborgerskap fra" />
					)}
					{!isDoedfoedt && <Diskresjonskoder basePath={path} formikBag={formikBag} />}
					{!isDoedfoedt && <Alder basePath={path} formikBag={formikBag} title="Alder" />}
					{isDoedfoedt && (
						<FormikDatepicker
							name={`${path}.foedselsdato`}
							label="Dato født"
							onChange={dato => handleFoedselsdatoChange(path, dato)}
						/>
					)}
				</React.Fragment>
			)}
		</FormikDollyFieldArray>
	)
}
