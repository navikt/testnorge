import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { AdresseKodeverk } from '~/config/kodeverk'

export const Visning = ({ data, relasjoner }) => {
	//TODO Lag relatert person som egen komponent som kan brukes av alle
	const retatertPersonIdent = data.relatertVedSivilstand
	const retatertPerson = relasjoner.find(
		(relasjon) => relasjon.relatertPerson?.ident === retatertPersonIdent
	)
	const { fornavn, mellomnavn, etternavn } = retatertPerson?.relatertPerson?.navn?.[0]

	return (
		<>
			<ErrorBoundary>
				<div className="person-visning_content">
					<TitleValue title="Type" value={data.type} />
					<TitleValue
						title="Gyldig fra og med"
						value={Formatters.formatDate(data.sivilstandsdato)}
					/>
				</div>
				<div className="person-visning_content">
					{/*TODO vise label ikke value på relasjontype*/}
					<h4 style={{ width: '100%', marginTop: '0' }}>{retatertPerson.relasjonType}</h4>
					<TitleValue title="Ident" value={retatertPerson?.relatertPerson?.ident} />
					<TitleValue title="Fornavn" value={fornavn} />
					<TitleValue title="Mellomnavn" value={mellomnavn} />
					<TitleValue title="Etternavn" value={etternavn} />
					<TitleValue title="Kjønn" value={retatertPerson?.relatertPerson?.kjoenn?.[0].kjoenn} />
					{/*	TODO Alder/fødselsdato*/}
					<TitleValue
						title="Statsborgerskap"
						value={retatertPerson?.relatertPerson?.statsborgerskap?.[0].landkode}
						kodeverk={AdresseKodeverk.StatsborgerskapLand}
					/>
				</div>
			</ErrorBoundary>
		</>
	)
}

export const Sivilstand = ({ data, relasjoner }) => {
	if (!data || data.length < 1) return null

	return (
		<div>
			<SubOverskrift label="Sivilstand" iconKind="partner" />

			<DollyFieldArray data={data} nested>
				{(sivilstand) => <Visning key={sivilstand.id} data={sivilstand} relasjoner={relasjoner} />}
			</DollyFieldArray>
		</div>
	)
}
