import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import Formatters from '@/utils/DataFormatter'
import { RelatertPerson } from '@/components/fagsystem/pdlf/visning/partials/RelatertPerson'
import { Relasjon, VergemaalValues } from '@/components/fagsystem/pdlf/PdlTypes'
import { VergemaalKodeverk } from '@/config/kodeverk'
import _cloneDeep from 'lodash/cloneDeep'
import { initialPdlPerson, initialVergemaal } from '@/components/fagsystem/pdlf/form/initialValues'
import _get from 'lodash/get'
import VisningRedigerbarConnector from '@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarConnector'

type VergemaalTypes = {
	data: Array<VergemaalValues>
	tmpPersoner?: Array<VergemaalValues>
	ident?: string
	erPdlVisning?: boolean
	relasjoner: Array<Relasjon>
}

export const Vergemaal = ({
	data,
	tmpPersoner,
	ident,
	erPdlVisning = false,
	relasjoner,
}: VergemaalTypes) => {
	if (!data || data.length < 1) {
		return null
	}

	const VergemaalLes = ({ vergemaalData, redigertRelatertePersoner = null, idx }) => {
		if (!vergemaalData) {
			return null
		}

		const retatertPersonIdent = vergemaalData.vergeIdent
		const relasjon = relasjoner?.find((item) => item.relatertPerson?.ident === retatertPersonIdent)
		const relasjonRedigert = redigertRelatertePersoner?.find(
			(item) => item.relatertPerson?.ident === retatertPersonIdent
		)

		const harFullmektig = vergemaalData.sakType === 'FRE'

		return (
			<>
				<div className="person-visning_redigerbar" key={idx}>
					<TitleValue
						title="Fylkesmannsembete"
						kodeverk={VergemaalKodeverk.Fylkesmannsembeter}
						value={vergemaalData.vergemaalEmbete || vergemaalData.embete}
					/>
					<TitleValue
						title="Sakstype"
						kodeverk={VergemaalKodeverk.Sakstype}
						value={vergemaalData.sakType || vergemaalData.type}
					/>
					<TitleValue
						title="Mandattype"
						kodeverk={VergemaalKodeverk.Mandattype}
						value={vergemaalData.mandatType || vergemaalData.vergeEllerFullmektig?.omfang}
					/>
					<TitleValue
						title="Gyldig f.o.m."
						value={Formatters.formatDate(vergemaalData.gyldigFraOgMed)}
					/>
					<TitleValue
						title="Gyldig t.o.m."
						value={Formatters.formatDate(vergemaalData.gyldigTilOgMed)}
					/>
					{!relasjon && !relasjonRedigert && (
						<TitleValue
							title={harFullmektig ? 'Fullmektig' : 'Verge'}
							value={
								vergemaalData.vergeIdent || vergemaalData.vergeEllerFullmektig?.motpartsPersonident
							}
						/>
					)}
				</div>
				{(relasjonRedigert || relasjon) && (
					<RelatertPerson
						data={relasjonRedigert?.relatertPerson || relasjon?.relatertPerson}
						tittel={harFullmektig ? 'Fullmektig' : 'Verge'}
					/>
				)}
			</>
		)
	}

	const VergemaalVisning = ({ vergemaalData, idx }) => {
		const initVergemaal = Object.assign(_cloneDeep(initialVergemaal), data[idx])
		let initialValues = { vergemaal: initVergemaal }
		initialValues.vergemaal.nyVergeIdent = initialPdlPerson

		const redigertVergemaalPdlf = _get(tmpPersoner, `${ident}.person.vergemaal`)?.find(
			(a: VergemaalValues) => a.id === vergemaalData.id
		)
		const redigertRelatertePersoner = _get(tmpPersoner, `${ident}.relasjoner`)

		const slettetVergemaalPdlf = tmpPersoner?.hasOwnProperty(ident) && !redigertVergemaalPdlf
		if (slettetVergemaalPdlf) {
			return <pre style={{ margin: '0' }}>Opplysning slettet</pre>
		}

		const vergemaalValues = redigertVergemaalPdlf ? redigertVergemaalPdlf : vergemaalData
		let redigertVergemaalValues = redigertVergemaalPdlf
			? {
					vergemaal: Object.assign(_cloneDeep(initialVergemaal), redigertVergemaalPdlf),
			  }
			: null
		if (redigertVergemaalValues) {
			redigertVergemaalValues.vergemaal.nyVergeIdent = initialPdlPerson
		}

		const eksisterendeNyPerson = redigertRelatertePersoner
			? {
					value: redigertRelatertePersoner[0]?.relatertPerson?.ident,
					label: `${redigertRelatertePersoner[0]?.relatertPerson?.ident} - ${redigertRelatertePersoner?.[0]?.relatertPerson?.navn?.[0]?.fornavn} ${redigertRelatertePersoner?.[0]?.relatertPerson?.navn?.[0]?.etternavn}`,
			  }
			: {
					value: relasjoner?.[0]?.relatertPerson?.ident,
					label: `${relasjoner?.[0]?.relatertPerson?.ident} - ${relasjoner?.[0]?.relatertPerson?.navn?.[0]?.fornavn} ${relasjoner?.[0]?.relatertPerson?.navn?.[0]?.etternavn}`,
			  }

		return erPdlVisning ? (
			<VergemaalLes vergemaalData={vergemaalData} idx={idx} />
		) : (
			<VisningRedigerbarConnector
				dataVisning={
					<VergemaalLes
						vergemaalData={vergemaalValues}
						redigertRelatertePersoner={redigertRelatertePersoner}
						idx={idx}
					/>
				}
				initialValues={initialValues}
				eksisterendeNyPerson={eksisterendeNyPerson}
				redigertAttributt={redigertVergemaalValues}
				path="vergemaal"
				ident={ident}
			/>
		)
	}

	return (
		<div>
			<SubOverskrift label="Vergemål" iconKind="vergemaal" />
			<div className="person-visning_content">
				<ErrorBoundary>
					<DollyFieldArray data={data} nested>
						{(vergemaal: VergemaalValues, idx: number) => (
							<VergemaalVisning vergemaalData={vergemaal} idx={idx} />
						)}
					</DollyFieldArray>
				</ErrorBoundary>
			</div>
		</div>
	)
}
