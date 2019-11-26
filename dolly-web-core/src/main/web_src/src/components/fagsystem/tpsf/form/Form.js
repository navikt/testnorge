import React from 'react'
import { subYears } from 'date-fns'
import * as Yup from 'yup'
import { Personinformasjon } from './personinformasjon/Personinformasjon'
import { Adresser } from './adresser/Adresser'
import { requiredDate } from '~/utils/YupValidations'

export const TpsfForm = ({ formikBag }) => {
	return (
		<React.Fragment>
			<Personinformasjon formikBag={formikBag} />
			<Adresser formikBag={formikBag} />
		</React.Fragment>
	)
}

TpsfForm.initialValues = attrs => {
	const initial = {
		tpsf: {}
	}

	if (attrs.foedtEtter) initial.tpsf.foedtEtter = subYears(new Date(), 80)
	if (attrs.foedtFoer) initial.tpsf.foedtFoer = new Date()
	if (attrs.doedsdato) initial.tpsf.doedsdato = null

	if (attrs.statsborgerskap) {
		initial.tpsf.statsborgerskap = ''
		initial.tpsf.statsborgerskapRegdato = null
	}

	if (attrs.innvandretFraLand) {
		initial.tpsf.innvandretFraLand = ''
		initial.tpsf.innvandretFraLandFlyttedato = null
	}

	if (attrs.utvandretTilLand) {
		initial.tpsf.utvandretTilLand = ''
		initial.tpsf.utvandretTilLandFlyttedato = null
	}

	if (attrs.identHistorikk)
		initial.tpsf.identHistorikk = [
			{
				foedtEtter: '',
				foedtFoer: '',
				identtype: '',
				kjonn: '',
				regdato: ''
			}
		]

	if (attrs.kjonn) initial.tpsf.kjonn = ''
	if (attrs.harMellomnavn) initial.tpsf.harMellomnavn = true
	if (attrs.sivilstand) initial.tpsf.sivilstand = ''
	if (attrs.sprakKode) initial.tpsf.sprakKode = ''
	if (attrs.egenAnsattDatoFom) initial.tpsf.egenAnsattDatoFom = new Date()
	if (attrs.erForsvunnet) {
		initial.tpsf.erForsvunnet = true
		initial.tpsf.forsvunnetDato = null
	}

	if (attrs.boadresse) {
		initial.tpsf.boadresse = null
		initial.tpsf.adresseNrInfo = null
	}
	if (attrs.postadresse) initial.tpsf.postadresse = {}

	return initial
}

TpsfForm.validation = {
	tpsf: Yup.object({
		foedtEtter: requiredDate,
		foedtFoer: Yup.date().nullable(),
		doedsdato: Yup.date().nullable(),
		statsborgerskap: Yup.string(),
		statsborgerskapRegdato: Yup.date().nullable(),
		innvandretFraLand: Yup.string(),
		innvandretFraLandFlyttedato: Yup.date().nullable(),
		utvandretTilLand: Yup.string(),
		utvandretTilLandFlyttedato: Yup.date().nullable()
	})
}
