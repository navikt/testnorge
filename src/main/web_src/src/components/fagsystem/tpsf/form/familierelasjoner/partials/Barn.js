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
import _get from 'lodash/get'

const initialValues = {
	identtype: 'FNR',
	kjonn: '',
	barnType: '',
	partnerNr: null,
	borHos: '',
	erAdoptert: false,
	alder: Formatters.randomIntInRange(0, 17),
	doedsdato: null,
	spesreg: '',
	utenFastBopel: false,
	statsborgerskap: '',
	statsborgerskapRegdato: '',
	statsborgerskapTildato: ''
}

export const initialValuesDoedfoedt = {
	identtype: 'FDAT',
	kjonn: '',
	barnType: '',
	partnerNr: null,
	foedselsdato: '',
	foedtEtter: '',
	foedtFoer: '',
	doedsdato: ''
}

export const Barn = ({ formikBag, personFoerLeggTil }) => {
	const handleIdenttypeChange = (path, ident, isDoedfoedt) => {
		if (ident.value === 'FDAT') {
			formikBag.setFieldValue(`${path}`, initialValuesDoedfoedt)
		} else if (isDoedfoedt) {
			formikBag.setFieldValue(`${path}`, initialValues)
		}
		formikBag.setFieldValue(`${path}.identtype`, ident.value)
	}

	const handleFoedselsdatoChange = (path, dato) => {
		formikBag.setFieldValue(`${path}.foedselsdato`, dato)
		formikBag.setFieldValue(`${path}.doedsdato`, dato)
		formikBag.setFieldValue(`${path}.foedtEtter`, dato)
		formikBag.setFieldValue(`${path}.foedtFoer`, dato)
	}

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

	return (
		<FormikDollyFieldArray name="tpsf.relasjoner.barn" header="Barn" newEntry={initialValues}>
			{(path, idx) => {
				const isDoedfoedt = _get(formikBag.values, `${path}.identtype`) === 'FDAT'
				const eksisterendeBarn = _get(formikBag.values, `${path}.ident`)
				const aktuellRelasjon =
					personFoerLeggTil &&
					eksisterendeBarn &&
					_get(personFoerLeggTil, 'tpsf.relasjoner').filter(
						relasjon => relasjon.personRelasjonMed.ident === eksisterendeBarn
					)
				const fornavn = aktuellRelasjon && aktuellRelasjon[0].personRelasjonMed.fornavn
				const etternavn = aktuellRelasjon && aktuellRelasjon[0].personRelasjonMed.etternavn

				return !eksisterendeBarn ? (
					<React.Fragment key={idx}>
						<FormikSelect
							name={`${path}.identtype`}
							label="Identtype"
							options={Options('identtypeBarn')}
							onChange={ident => handleIdenttypeChange(path, ident, isDoedfoedt)}
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
							<FormikDatepicker
								name={`${path}.statsborgerskapRegdato`}
								label="Statsborgerskap fra"
							/>
						)}
						{!isDoedfoedt && (
							<FormikDatepicker
								name={`${path}.statsborgerskapTildato`}
								label="Statsborgerskap til"
							/>
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
				) : (
					<>
						<h4>
							{fornavn} {etternavn} ({eksisterendeBarn})
						</h4>
						<div className="alder-component">
							<FormikDatepicker name={`${path}.doedsdato`} label="Dødsdato" />
						</div>
					</>
				)
			}}
		</FormikDollyFieldArray>
	)
}
