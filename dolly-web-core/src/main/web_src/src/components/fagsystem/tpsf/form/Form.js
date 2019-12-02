import React from 'react'
import _isEmpty from 'lodash/isEmpty'
import { subYears } from 'date-fns'
import * as Yup from 'yup'
import { Personinformasjon } from './personinformasjon/Personinformasjon'
import { Adresser } from './adresser/Adresser'
import { requiredDate, requiredString } from '~/utils/YupValidations'

export const TpsfForm = ({ formikBag }) => {
	return (
		<React.Fragment>
			<Personinformasjon formikBag={formikBag} />
			<Adresser formikBag={formikBag} />
		</React.Fragment>
	)
}

TpsfForm.initialValues = attrs => {
	const initial = {}

	if (attrs.foedtEtter) initial.foedtEtter = subYears(new Date(), 80)
	if (attrs.foedtFoer) initial.foedtFoer = new Date()
	if (attrs.doedsdato) initial.doedsdato = null

	if (attrs.statsborgerskap) {
		initial.statsborgerskap = ''
		initial.statsborgerskapRegdato = null
	}

	if (attrs.innvandretFraLand) {
		initial.innvandretFraLand = ''
		initial.innvandretFraLandFlyttedato = null
	}

	if (attrs.utvandretTilLand) {
		initial.utvandretTilLand = ''
		initial.utvandretTilLandFlyttedato = null
	}

	if (attrs.identHistorikk)
		initial.identHistorikk = [
			{
				foedtEtter: '',
				foedtFoer: '',
				identtype: '',
				kjonn: '',
				regdato: ''
			}
		]

	if (attrs.kjonn) initial.kjonn = ''
	if (attrs.harMellomnavn) initial.harMellomnavn = true
	if (attrs.sivilstand) initial.sivilstand = ''
	if (attrs.sprakKode) initial.sprakKode = ''
	if (attrs.egenAnsattDatoFom) initial.egenAnsattDatoFom = new Date()
	if (attrs.spesreg) {
		initial.spesreg = ''
		initial.utenFastBopel = false
	}
	if (attrs.erForsvunnet) {
		initial.erForsvunnet = true
		initial.forsvunnetDato = null
	}

	if (attrs.boadresse) {
		initial.boadresse = null
		initial.adresseNrInfo = null
	}
	if (attrs.postadresse) initial.postadresse = {}

	return !_isEmpty(initial) && { tpsf: initial }
}

TpsfForm.validation = {
	tpsf: Yup.object({
		foedtEtter: Yup.date().nullable(),
		foedtFoer: Yup.date().nullable(),
		doedsdato: Yup.date().nullable(),
		statsborgerskap: Yup.string(),
		statsborgerskapRegdato: Yup.date().nullable(),
		innvandretFraLand: Yup.string(),
		innvandretFraLandFlyttedato: Yup.date().nullable(),
		utvandretTilLand: Yup.string(),
		utvandretTilLandFlyttedato: Yup.date().nullable(),
		spesreg: Yup.string().when('utenFastBopel', {
			is: true,
			then: Yup.string().test(
				'is-not-kode6',
				'Kan ikke være "Kode 6" når "Uten fast bopel" er valgt.',
				value => value !== 'SPSF'
			),
			otherwise: requiredString
		})
	})
}
