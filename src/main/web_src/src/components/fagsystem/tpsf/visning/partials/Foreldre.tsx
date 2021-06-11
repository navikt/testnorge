import React, { useEffect, useState } from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { TpsfApi } from '~/service/Api'
import { Adressevisning } from './Boadresse'
import { PostadresseVisning } from './Postadresse'
import { Historikk } from '~/components/ui/historikk/Historikk'
import { PersoninformasjonKodeverk } from '~/config/kodeverk'

type Data = {
	person: Person
	type: string
}

type Relasjon = [
	{
		id: number
		personRelasjonMed: {
			ident: string
			fornavn: string
			etternavn: string
			kjonn: string
		}
		relasjonTypeNavn: string
	}
]

type Person = {
	ident: string
	identtype: string
	fornavn: string
	mellomnavn?: string
	etternavn: string
	kjonn: string
	alder: number
	doedsdato: Date
	foreldreType: string
	spesreg: string
	utenFastBopel: boolean
	boadresse: string
	postadresse: string
}

export const Foreldre = ({ person, type }: Data) => {
	if (!person) return null
	const [foreldreInfo, setForeldreInfo] = useState(null)
	const [isLoading, setIsLoading] = useState(false)

	useEffect(() => {
		const fetchData = async () => {
			setIsLoading(true)
			const respons = await TpsfApi.getPersoner([person.ident])
			setForeldreInfo(respons.data)
			setIsLoading(false)
		}
		fetchData()
	}, [])

	return (
		<>
			<div className="person-visning_content">
				<TitleValue title={person.identtype} value={person.ident} />
				<TitleValue title="Fornavn" value={person.fornavn} />
				<TitleValue title="Mellomnavn" value={person.mellomnavn} />
				<TitleValue title="Etternavn" value={person.etternavn} />
				<TitleValue title="Kjønn" value={Formatters.kjonn(person.kjonn, person.alder)} />
				<TitleValue
					title="Alder"
					value={person.doedsdato ? `${person.alder}(død)` : person.alder}
				/>
				<TitleValue title="Dødsdato" value={Formatters.formatDate(person.doedsdato)} />
				<TitleValue title="Foreldretype" value={type} />
				<TitleValue
					title="Diskresjonskode"
					kodeverk={PersoninformasjonKodeverk.Diskresjonskoder}
					value={person.spesreg}
				/>
				<TitleValue title="Uten fast bopel" value={person.utenFastBopel && 'Ja'} />
				{foreldreInfo && !isLoading && foreldreInfo.length > 0 && (
					<TitleValue title="Barn" value={finnBarn(foreldreInfo[0].relasjoner).join(', ')} />
				)}
			</div>
			{person.boadresse.length > 0 && (
				<Historikk component={Adressevisning} propName="boadresse" data={person.boadresse} />
			)}
			{person.postadresse.length > 0 && (
				<Historikk
					component={PostadresseVisning}
					propName="postadresse"
					data={person.postadresse}
				/>
			)}
		</>
	)
}

const finnBarn = (relasjoner: Relasjon) =>
	relasjoner
		.filter(relasjon => {
			return relasjon.relasjonTypeNavn === 'BARN'
		})
		.map(
			relasjon =>
				relasjon.personRelasjonMed.fornavn +
				' ' +
				relasjon.personRelasjonMed.etternavn +
				' (' +
				(relasjon.personRelasjonMed.kjonn.includes('K') ? 'DATTER' : 'SØNN') +
				')'
		)
