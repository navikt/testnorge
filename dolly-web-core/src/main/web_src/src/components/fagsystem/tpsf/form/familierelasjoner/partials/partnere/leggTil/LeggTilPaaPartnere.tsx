import * as React from 'react'
import { get as _get } from 'lodash'
import { head as _head } from 'lodash'
import { has as _has } from 'lodash'
// @ts-ignore
import useBoolean from '~/utils/hooks/useBoolean'
import { Partnere, sjekkKanOppretteNyPartner } from '../Partnere'
import { Sivilstand } from '../Sivilstand'
import { FieldArrayAddButton } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { erOpprettNyPartnerGyldig } from '../SivilstandOptions'
import TidligerePartner from './TidligerePartner'
import Formatters from '~/utils/DataFormatter'
import { FormikProps } from 'formik'

interface LeggTilPaaPartnere {
	formikBag: FormikProps<{}>
	personFoerLeggTil: any
}

type TidligerePartner = {
	sistePartner: any
	formikBag: FormikProps<{}>
	sisteSivilstand: SivilstandObj
}

type PersonBestilling = {
	identtype: string
}
type PersonHentet = {
	relasjoner?: Array<Relasjon>
}

type Relasjon = {
	person: PersonHentet
	personRelasjonMed: PersonHentet
	relasjonTypeNavn: string
}
type SivilstandObj = {
	sivilstand: string
	sivilstandRegdato: string
}
const initialSivilstand: SivilstandObj = { sivilstand: '', sivilstandRegdato: '' }

const initialValues = {
	identtype: 'FNR',
	kjonn: '',
	sivilstander: [{ sivilstand: '', sivilstandRegdato: '' }],
	harFellesAdresse: false,
	alder: Formatters.randomIntInRange(30, 60),
	spesreg: '',
	utenFastBopel: false,
	statsborgerskap: '',
	statsborgerskapRegdato: ''
}

const partnerPath: string = 'tpsf.relasjoner.partnere'
const tidligerePartnere = (personFoerLeggTil: PersonHentet) =>
	_get(personFoerLeggTil, 'relasjoner', [])
		.filter((relasjon: Relasjon) => relasjon.relasjonTypeNavn === 'PARTNER')
		.map((relasjon: Relasjon) => relasjon.personRelasjonMed)

const oppdatertTidligerePartner = (formikBag: FormikProps<{}>): boolean =>
	_has(formikBag.values, `${partnerPath}[0].sivilstander`) &&
	!_has(formikBag.values, `${partnerPath}[0].identtype`)

const harNyPartner = (formikBag: FormikProps<{}>): boolean =>
	_get(formikBag.values, partnerPath).some((partner: PersonBestilling) =>
		_has(partner, 'identtype')
	)

export default ({ formikBag, personFoerLeggTil }: LeggTilPaaPartnere) => {
	const [nyPartner, lagtTilNyPartner, fjernNyPartner] = useBoolean(harNyPartner(formikBag))
	const sistePartner = _head(tidligerePartnere(personFoerLeggTil))

	//Ingen partner fra før
	if (!sistePartner) return <Partnere formikBag={formikBag} />

	const sisteSivilstand: SivilstandObj = _head(_get(sistePartner, 'sivilstander'))

	//Sjekker sivilstand på siste tidligere partner og siste nye partner
	const kanOppretteFoersteNyePartner: boolean = oppdatertTidligerePartner(formikBag)
		? sjekkKanOppretteNyPartner(_get(formikBag.values, partnerPath), formikBag)
		: erOpprettNyPartnerGyldig(sisteSivilstand.sivilstand)

	const addNewPartner = () => {
		lagtTilNyPartner()
		const partnerNr: number = oppdatertTidligerePartner(formikBag) ? 1 : 0
		return formikBag.setFieldValue(`${partnerPath}[${partnerNr}]`, initialValues)
	}
	return (
		<>
			<div style={{ backgroundColor: '#f1f1f18c', width: '100%' }}>
				<TidligerePartner
					sistePartner={sistePartner}
					sisteSivilstand={sisteSivilstand}
					formikBag={formikBag}
				/>

				{!nyPartner && !oppdatertTidligerePartner(formikBag) && (
					<LeggTilFlereSivilstander formikBag={formikBag} />
				)}
			</div>
			//TODO Hvordan oppdatere oppdatererTidligerePartnere når sivilstand slettes?
			{oppdatertTidligerePartner(formikBag) && (
				<Sivilstand
					basePath={`${partnerPath}[0].sivilstander`}
					formikBag={formikBag}
					locked={nyPartner}
					erSistePartner={!nyPartner}
					sisteSivilstand={sisteSivilstand}
				/>
			)}
			{nyPartner ? (
				<Partnere
					formikBag={formikBag}
					oppdatertSistePartner={oppdatertTidligerePartner(formikBag)}
					sisteSivilstandForrigePartner={sisteSivilstand}
				/>
			) : (
				<FieldArrayAddButton
					hoverText={
						!kanOppretteFoersteNyePartner
							? 'Forhold med tidligere partner må avsluttes (skilt eller enke/-mann)'
							: false
					}
					addEntryButtonText="Legg til ny partner"
					onClick={addNewPartner}
					disabled={!kanOppretteFoersteNyePartner}
				/>
			)}
		</>
	)
}

type LeggTilFlereSivilstander = {
	formikBag: FormikProps<{}>
}

const LeggTilFlereSivilstander = ({ formikBag }: LeggTilFlereSivilstander) => {
	const addNewSivilstand = () => {
		formikBag.setFieldValue(`${partnerPath}[0]`, { sivilstander: [initialSivilstand] })
	}
	return (
		<FieldArrayAddButton
			addEntryButtonText="Nytt forhold"
			hoverText="Legg til nytt forhold"
			onClick={addNewSivilstand}
			disabled={false}
		/>
	)
}
