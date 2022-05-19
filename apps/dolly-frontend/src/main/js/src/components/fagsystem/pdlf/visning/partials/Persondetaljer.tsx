import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import Formatters from '~/utils/DataFormatter'
import { TpsMPersonInfo } from '~/components/fagsystem/pdl/visning/partials/tpsMessaging/TpsMPersonInfo'
import _get from 'lodash/get'
import _cloneDeep from 'lodash/cloneDeep'
import {
	initialKjoenn,
	initialNavn,
	initialPersonstatus,
} from '~/components/fagsystem/pdlf/form/initialValues'
import VisningRedigerbarConnector from '~/components/fagsystem/pdlf/visning/VisningRedigerbarConnector'
import VisningRedigerbarPersondetaljerConnector from '~/components/fagsystem/pdlf/visning/VisningRedigerbarPersondetaljerConnector'

const getCurrentPersonstatus = (data) => {
	if (data?.folkeregisterpersonstatus && data?.folkeregisterpersonstatus?.[0] !== null) {
		const statuser = data.folkeregisterpersonstatus.filter((status) => {
			return !status?.metadata?.historisk
		})
		return statuser.length > 0 ? statuser[0] : null
	} else if (data?.folkeregisterPersonstatus && data?.folkeregisterPersonstatus?.[0] !== null) {
		const statuser = data.folkeregisterPersonstatus
		return statuser.length > 0 ? statuser[0] : null
	}
	return null
}

export const Persondetaljer = ({
	data,
	tpsMessagingData,
	tpsMessagingLoading = false,
	visTittel = true,
	tmpPersoner,
	ident,
	erPdlVisning = false,
}) => {
	if (!data) {
		return null
	}
	const redigertPerson = _get(tmpPersoner, `${data?.ident}.person`)

	const initialPersondetaljer = {
		navn: [initialNavn],
		kjoenn: [initialKjoenn],
		folkeregisterpersonstatus: [initialPersonstatus],
	}

	const PersondetaljerLes = ({ person, idx }) => {
		const personNavn = person?.navn?.[0]
		const { fornavn, mellomnavn, etternavn } = personNavn
		const personKjoenn = person?.kjoenn?.[0]
		const personstatus = getCurrentPersonstatus(redigertPerson || person)

		return (
			<div className="person-visning_redigerbar">
				<TitleValue title="Identtttt" value={person?.ident} />
				<TitleValue title="Fornavn" value={fornavn} />
				<TitleValue title="Mellomnavn" value={mellomnavn} />
				<TitleValue title="Etternavn" value={etternavn} />
				<TitleValue title="Kjønn" value={personKjoenn?.kjoenn} />
				<TitleValue
					title="Personstatus"
					value={Formatters.showLabel('personstatus', personstatus?.status)}
				/>
				{/*<TpsMPersonInfo data={tpsMessagingData} loading={tpsMessagingLoading} />*/}
			</div>
		)
	}

	const PersondetaljerVisning = ({ person, idx }) => {
		console.log('data: ', data) //TODO - SLETT MEG
		// const initPerson = Object.assign(_cloneDeep(initialPersondetaljer), data)
		const initPerson = {
			navn: [data?.navn?.[0] || initialNavn],
			kjoenn: [data?.kjoenn?.[0] || initialKjoenn],
			folkeregisterpersonstatus: [data?.folkeregisterPersonstatus?.[0] || initialPersonstatus],
		}
		// console.log('initPerson: ', initPerson) //TODO - SLETT MEG
		// console.log('data: ', data) //TODO - SLETT MEG
		// const initialValues = { adressebeskyttelse: initAdressebeskyttelse }

		const redigertPersonPdlf = _get(tmpPersoner, `${ident}.person`)
		// const slettetAdressebeskyttelsePdlf =
		// 	tmpPersoner?.hasOwnProperty(ident) && !redigertAdressebeskyttelsePdlf
		// if (slettetAdressebeskyttelsePdlf) return <pre style={{ margin: '0' }}>Opplysning slettet</pre>

		const personValues = redigertPersonPdlf ? redigertPersonPdlf : person
		const redigertPersonValues = redigertPersonPdlf
			? // ? Object.assign(_cloneDeep(initialPersondetaljer), redigertPersonPdlf)
			  {
					navn: [redigertPersonPdlf?.navn?.[0]] || null,
					kjoenn: [redigertPersonPdlf?.kjoenn?.[0]] || null,
					folkeregisterpersonstatus: [redigertPersonPdlf?.folkeregisterPersonstatus?.[0]] || null,
			  }
			: null
		// console.log('erPdlVisning: ', erPdlVisning) //TODO - SLETT MEG
		return erPdlVisning ? (
			<PersondetaljerLes person={person} idx={0} />
		) : (
			<VisningRedigerbarPersondetaljerConnector
				dataVisning={<PersondetaljerLes person={personValues} idx={0} />}
				initialValues={initPerson}
				redigertAttributt={redigertPersonValues}
				path="person"
				ident={ident}
			/>
		)
	}

	// console.log('data: ', data) //TODO - SLETT MEG
	return (
		<ErrorBoundary>
			<div>
				<SubOverskrift label="Persondetaljer" iconKind="personinformasjon" />
				<div className="person-visning_content">
					<PersondetaljerVisning person={data} idx={0} />
				</div>
			</div>
		</ErrorBoundary>
	)
}
