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

interface LeggTilPaaPartnere {
	formikBag: any
	personFoerLeggTil: any
}

type TidligerePartner = {
	sistePartner: any
	formikBag: any
	sisteSivilstand: SivilstandObj
}

type SivilstandObj = {
	sivilstand: string
	sivilstandRegdato: string | null
}
const initialSivilstand: SivilstandObj = { sivilstand: '', sivilstandRegdato: null }

const initialValues = {
	identtype: 'FNR',
	kjonn: '',
	sivilstander: [{ sivilstand: '', sivilstandRegdato: null }],
	harFellesAdresse: false,
	alder: Formatters.randomIntInRange(30, 60),
	spesreg: '',
	utenFastBopel: false,
	statsborgerskap: '',
	statsborgerskapRegdato: ''
}

const partnerPath: string = 'tpsf.relasjoner.partnere'
const tidligerePartnere = personFoerLeggTil =>
	_get(personFoerLeggTil, 'relasjoner', [])
		.filter((relasjon: any) => relasjon.relasjonTypeNavn === 'PARTNER')
		.map((relasjon: any) => relasjon.personRelasjonMed)

export default ({ formikBag, personFoerLeggTil }: LeggTilPaaPartnere) => {
	const [oppdatererTidligerePartner, oppdaterer, oppdatererIkke] = useBoolean(false)
	const [nyPartner, lagtTilNyPartner, fjernNyPartner] = useBoolean(false)

	const sistePartner = _head(tidligerePartnere(personFoerLeggTil))

	//Ingen partner fra før
	if (!sistePartner) return <Partnere formikBag={formikBag} />

	//Alternativ ([]) er ikke sivilstandobj (!)
	const sisteSivilstand: SivilstandObj = _head(_get(sistePartner, 'sivilstander', []))

	//Sjekker siste tidligere partner og siste nye partner
	const kanOppretteFoersteNyePartner: boolean = oppdatererTidligerePartner
		? sjekkKanOppretteNyPartner(_get(formikBag.values, partnerPath), formikBag)
		: erOpprettNyPartnerGyldig(sisteSivilstand.sivilstand)

	const addNewPartner = () => {
		lagtTilNyPartner()
		const partnerNr: number = oppdatererTidligerePartner ? 1 : 0
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
				{!nyPartner && !oppdatererTidligerePartner && (
					<LeggTilFlereSivilstander formikBag={formikBag} oppdatererTidligerePartner={oppdaterer} />
				)}
			</div>
			{oppdatererTidligerePartner && (
				<Sivilstand
					basePath={`${partnerPath}[0].sivilstander`}
					formikBag={formikBag}
					locked={nyPartner}
					erSistePartner={!nyPartner}
					sisteSivilstandKode={sisteSivilstand.sivilstand}
					minimumDato={sisteSivilstand.sivilstandRegdato}
				/>
			)}

			{nyPartner ? (
				<Partnere formikBag={formikBag} eksisterendePartner={oppdatererTidligerePartner} />
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

const LeggTilFlereSivilstander = ({ formikBag, oppdatererTidligerePartner }) => {
	const addNewSivilstand = () => {
		oppdatererTidligerePartner()
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
