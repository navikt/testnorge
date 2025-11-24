import { AdresseKodeverk } from '@/config/kodeverk'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { Personnavn } from '@/components/fagsystem/pdlf/visning/partials/Personnavn'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { RelatertPerson } from '@/components/fagsystem/pdlf/visning/partials/RelatertPerson'
import * as _ from 'lodash-es'
import { initialKontaktinfoForDoedebo } from '@/components/fagsystem/pdlf/form/initialValues'
import { getEksisterendeNyPerson } from '@/components/fagsystem/utils'
import VisningRedigerbarConnector from '@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarConnector'
import { formatDate } from '@/utils/DataFormatter'
import { OpplysningSlettet } from '@/components/fagsystem/pdlf/visning/visningRedigerbar/OpplysningSlettet'
import React from 'react'

const KontaktinformasjonForDoedsboLes = ({
	data,
	relasjoner,
	redigertRelatertePersoner = null,
	idx,
}) => {
	if (!data) {
		return null
	}

	const kontaktpersonIdent = data.personSomKontakt?.identifikasjonsnummer
	const kontaktperson = relasjoner?.find(
		(relasjon) => relasjon.relatertPerson?.ident === kontaktpersonIdent,
	)
	const kontaktpersonRedigert = redigertRelatertePersoner?.find(
		(item) => item.relatertPerson?.ident === kontaktpersonIdent,
	)

	const {
		skifteform,
		attestutstedelsesdato,
		adresse,
		advokatSomKontakt,
		organisasjonSomKontakt,
		personSomKontakt,
	} = data

	const getKontakttype = () => {
		if (advokatSomKontakt) {
			return 'Advokat'
		} else if (personSomKontakt) {
			return 'Person'
		} else if (organisasjonSomKontakt) {
			return 'Organisasjon'
		} else return null
	}

	return (
		<>
			<div className="person-visning_redigerbar" key={idx}>
				<div className="person-visning_content">
					<TitleValue title="Skifteform" value={skifteform} />
					<TitleValue
						title="Utstedelsesdato skifteattest"
						value={formatDate(attestutstedelsesdato)}
					/>
				</div>
				{!kontaktperson && !kontaktpersonRedigert && (
					<div className="person-visning_content">
						<h4 style={{ width: '100%', marginTop: '0' }}>{getKontakttype()} som kontakt</h4>
						<TitleValue
							title="Organisasjonsnummer"
							value={
								advokatSomKontakt?.organisasjonsnummer ||
								organisasjonSomKontakt?.organisasjonsnummer
							}
						/>
						<TitleValue
							title="Organisasjonsnavn"
							value={
								advokatSomKontakt?.organisasjonsnavn || organisasjonSomKontakt?.organisasjonsnavn
							}
						/>
						<TitleValue
							title="Identifikasjonsnummer"
							value={personSomKontakt?.identifikasjonsnummer}
						/>
						<TitleValue title="Fødselsdato" value={formatDate(personSomKontakt?.foedselsdato)} />
						<Personnavn
							data={
								advokatSomKontakt?.kontaktperson ||
								advokatSomKontakt?.personnavn ||
								organisasjonSomKontakt?.kontaktperson
							}
						/>
						<Personnavn data={personSomKontakt?.navn || personSomKontakt?.personnavn} />
					</div>
				)}
			</div>
			<div className="person-visning_content">
				<h4 style={{ width: '100%', marginTop: '0' }}>Adresse</h4>
				<TitleValue
					title="Land"
					value={adresse?.landkode}
					kodeverk={AdresseKodeverk.PostadresseLand}
				/>
				<TitleValue title="Adresselinje 1" value={adresse?.adresselinje1} />
				<TitleValue title="Adresselinje 2" value={adresse?.adresselinje2} />
				<TitleValue
					title="Postnummer og -sted"
					value={adresse?.postnummer + ' ' + adresse?.poststedsnavn}
				/>
			</div>
			{(kontaktperson || kontaktpersonRedigert) && (
				<RelatertPerson
					data={kontaktpersonRedigert?.relatertPerson || kontaktperson.relatertPerson}
					tittel="Kontaktperson"
				/>
			)}
		</>
	)
}

export const KontaktinformasjonForDoedsboVisning = ({
	kontaktinfoData,
	idx,
	data,
	tmpPersoner,
	ident,
	erPdlVisning,
	relasjoner,
}) => {
	const initKontaktinfo = Object.assign(_.cloneDeep(initialKontaktinfoForDoedebo), data[idx])
	let initialValues = { kontaktinformasjonForDoedsbo: initKontaktinfo }

	const redigertKontaktinfoPdlf = _.get(
		tmpPersoner,
		`${ident}.person.kontaktinformasjonForDoedsbo`,
	)?.find((a) => a.id === kontaktinfoData.id)
	const redigertRelatertePersoner = _.get(tmpPersoner, `${ident}.relasjoner`)

	const slettetKontaktinfoPdlf = tmpPersoner?.hasOwnProperty(ident) && !redigertKontaktinfoPdlf
	if (slettetKontaktinfoPdlf) {
		return <OpplysningSlettet />
	}

	const kontaktinfoValues = redigertKontaktinfoPdlf ? redigertKontaktinfoPdlf : kontaktinfoData
	let redigertKontaktinfoValues = redigertKontaktinfoPdlf
		? {
				kontaktinformasjonForDoedsbo: Object.assign(
					_.cloneDeep(initialKontaktinfoForDoedebo),
					redigertKontaktinfoPdlf,
				),
			}
		: null

	const eksisterendeNyPerson = redigertRelatertePersoner
		? getEksisterendeNyPerson(
				redigertRelatertePersoner,
				kontaktinfoValues?.personSomKontakt?.identifikasjonsnummer,
				['KONTAKT_FOR_DOEDSBO'],
			)
		: getEksisterendeNyPerson(
				relasjoner,
				kontaktinfoValues?.personSomKontakt?.identifikasjonsnummer,
				['KONTAKT_FOR_DOEDSBO'],
			)

	if (eksisterendeNyPerson && initialValues?.kontaktinformasjonForDoedsbo?.personSomKontakt) {
		const filteredPerson = Object.fromEntries(
			Object.entries(initialValues?.kontaktinformasjonForDoedsbo?.personSomKontakt).filter(
				(item) => item[0] !== 'nyKontaktperson',
			),
		)
		initialValues.kontaktinformasjonForDoedsbo.personSomKontakt = filteredPerson
	}

	if (
		eksisterendeNyPerson &&
		redigertKontaktinfoValues?.kontaktinformasjonForDoedsbo?.personSomKontakt
	) {
		const filteredPerson = Object.fromEntries(
			Object.entries(
				redigertKontaktinfoValues?.kontaktinformasjonForDoedsbo?.personSomKontakt,
			).filter((item) => item[0] !== 'nyKontaktperson'),
		)
		redigertKontaktinfoValues.kontaktinformasjonForDoedsbo.personSomKontakt = filteredPerson
	}

	return erPdlVisning ? (
		<KontaktinformasjonForDoedsboLes data={kontaktinfoData} relasjoner={relasjoner} idx={idx} />
	) : (
		<VisningRedigerbarConnector
			dataVisning={
				<KontaktinformasjonForDoedsboLes
					data={kontaktinfoValues}
					redigertRelatertePersoner={redigertRelatertePersoner}
					relasjoner={relasjoner}
					idx={idx}
				/>
			}
			initialValues={initialValues}
			eksisterendeNyPerson={eksisterendeNyPerson}
			redigertAttributt={redigertKontaktinfoValues}
			path="kontaktinformasjonForDoedsbo"
			ident={ident}
		/>
	)
}

export const KontaktinformasjonForDoedsbo = ({
	data,
	tmpPersoner,
	ident,
	erPdlVisning = false,
	relasjoner,
	erRedigerbar = true,
}) => {
	if (!data || data.length < 1) {
		return null
	}

	const kontaktpersonRelasjoner = relasjoner?.filter(
		(relasjon) => relasjon.relasjonType === 'KONTAKT_FOR_DOEDSBO',
	)

	return (
		<div>
			<SubOverskrift label="Kontaktinformasjon for dødsbo" iconKind="doedsbo" />
			<div className="person-visning_content">
				<ErrorBoundary>
					<DollyFieldArray data={data} nested>
						{(kontaktinfo, idx) =>
							erRedigerbar ? (
								<KontaktinformasjonForDoedsboVisning
									kontaktinfoData={kontaktinfo}
									idx={idx}
									data={data}
									tmpPersoner={tmpPersoner}
									ident={ident}
									erPdlVisning={erPdlVisning}
									relasjoner={kontaktpersonRelasjoner}
								/>
							) : (
								<KontaktinformasjonForDoedsboLes
									data={kontaktinfo}
									relasjoner={kontaktpersonRelasjoner}
									idx={idx}
								/>
							)
						}
					</DollyFieldArray>
				</ErrorBoundary>
			</div>
		</div>
	)
}
