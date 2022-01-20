import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { AdresseKodeverk } from '~/config/kodeverk'
import KodeverkConnector from '~/components/kodeverk/KodeverkConnector'

export const Visning = ({ data, relasjoner }) => {
	//TODO Lag relatert person som egen komponent som kan brukes av alle
	const retatertPersonIdent = data.relatertVedSivilstand
	const retatertPerson = relasjoner?.find(
		(relasjon) => relasjon.relatertPerson?.ident === retatertPersonIdent
	)

	return (
		<>
			<ErrorBoundary>
				<div className="person-visning_content">
					<TitleValue title="Type" value={Formatters.showLabel('sivilstandType', data.type)} />
					<TitleValue
						title="Gyldig fra og med"
						value={
							Formatters.formatDate(data.sivilstandsdato) ||
							Formatters.formatDate(data.gyldigFraOgMed)
						}
					/>
					{!relasjoner && <TitleValue title="Relatert person" value={data.relatertVedSivilstand} />}
				</div>
				{retatertPerson && (
					<div className="person-visning_content">
						<h4 style={{ width: '100%', marginTop: '0' }}>{'Ektefelle/partner'}</h4>
						<TitleValue title="Ident" value={retatertPerson?.relatertPerson?.ident} />
						<TitleValue title="Fornavn" value={retatertPerson?.relatertPerson?.navn?.[0].fornavn} />
						<TitleValue
							title="Mellomnavn"
							value={retatertPerson?.relatertPerson?.navn?.[0].mellomnavn}
						/>
						<TitleValue
							title="Etternavn"
							value={retatertPerson?.relatertPerson?.navn?.[0].etternavn}
						/>
						<TitleValue title="Kjønn" value={retatertPerson?.relatertPerson?.kjoenn?.[0].kjoenn} />
						<TitleValue
							title="Fødselsdato"
							value={Formatters.formatDate(
								retatertPerson?.relatertPerson?.foedsel?.[0].foedselsdato
							)}
						/>
						<TitleValue
							title="Statsborgerskap"
							value={retatertPerson?.relatertPerson?.statsborgerskap?.[0].landkode}
							kodeverk={AdresseKodeverk.StatsborgerskapLand}
						/>
						<TitleValue
							title="Gradering"
							value={Formatters.showLabel(
								'gradering',
								retatertPerson?.relatertPerson?.adressebeskyttelse?.[0].gradering
							)}
						/>
					</div>
				)}
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
