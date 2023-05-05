import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { Vegadresse } from '@/components/fagsystem/pdlf/visning/partials/Vegadresse'
import { Matrikkeladresse } from '@/components/fagsystem/pdlf/visning/partials/Matrikkeladresse'
import { UkjentBosted } from '@/components/fagsystem/pdlf/visning/partials/UkjentBosted'
import { FullmaktValues, PersonData } from '@/components/fagsystem/pdlf/PdlTypes'
import { initialDeltBosted } from '@/components/fagsystem/pdlf/form/initialValues'
import * as _ from 'lodash-es'
import VisningRedigerbarConnector from '@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarConnector'

type Data = {
	data: Array<any>
	tmpPersoner?: Array<PersonData>
	ident?: string
}

type AdresseProps = {
	adresse: any
	idx: number
}

export const Adresse = ({ adresse, idx }: AdresseProps) => {
	if (!adresse) {
		return null
	}

	return (
		<>
			{adresse.vegadresse && <Vegadresse adresse={adresse} idx={idx} />}
			{adresse.matrikkeladresse && <Matrikkeladresse adresse={adresse} idx={idx} />}
			{adresse.ukjentBosted && <UkjentBosted adresse={adresse} idx={idx} />}
		</>
	)
}

export const DeltBostedVisning = ({ adresseData, idx, data, tmpPersoner, ident }) => {
	const initBosted = Object.assign(_.cloneDeep(initialDeltBosted), data[idx])
	let initialValues = { deltBosted: initBosted }

	_.set(initialValues, 'deltBosted.adresseIdentifikatorFraMatrikkelen', undefined)

	//TODO: Lag DeltBostedValues
	const redigertBostedPdlf = _.get(tmpPersoner, `${ident}.person.deltBosted`)?.find(
		(a: FullmaktValues) => a.id === adresseData.id
	)
	// const redigertRelatertePersoner = _.get(tmpPersoner, `${ident}.relasjoner`)

	const slettetBostedtPdlf = tmpPersoner?.hasOwnProperty(ident) && !redigertBostedPdlf
	if (slettetBostedtPdlf) {
		return <pre style={{ margin: '0' }}>Opplysning slettet</pre>
	}

	const bostedValues = redigertBostedPdlf ? redigertBostedPdlf : adresseData
	let redigertBostedValues = redigertBostedPdlf
		? {
				deltBosted: Object.assign(_.cloneDeep(initialDeltBosted), redigertBostedPdlf),
		  }
		: null

	// const eksisterendeNyPerson = redigertRelatertePersoner
	// 	? getEksisterendeNyPerson(
	// 		redigertRelatertePersoner,
	// 		fullmaktValues?.motpartsPersonident,
	// 		'FULLMEKTIG'
	// 	)
	// 	: getEksisterendeNyPerson(relasjoner, fullmaktValues?.motpartsPersonident, 'FULLMEKTIG')

	// if (eksisterendeNyPerson && initialValues?.fullmakt?.nyFullmektig) {
	// 	initialValues.fullmakt.nyFullmektig = initialPdlPerson
	// }
	//
	// if (eksisterendeNyPerson && redigertFullmaktValues?.fullmakt?.nyFullmektig) {
	// 	redigertFullmaktValues.fullmakt.nyFullmektig = initialPdlPerson
	// }

	return (
		<VisningRedigerbarConnector
			dataVisning={<Adresse adresse={bostedValues} idx={idx} />}
			initialValues={initialValues}
			// eksisterendeNyPerson={eksisterendeNyPerson}
			redigertAttributt={redigertBostedValues}
			path="deltBosted"
			ident={ident}
		/>
	)
}

export const DeltBosted = ({ data, tmpPersoner, ident }: Data) => {
	if (!data || data.length === 0) {
		return null
	}

	return (
		<>
			<SubOverskrift label="Delt bosted" iconKind="adresse" />
			<div className="person-visning_content">
				<ErrorBoundary>
					<DollyFieldArray data={data} header="" nested>
						{(adresse: any, idx: number) => (
							<DeltBostedVisning
								adresseData={adresse}
								idx={idx}
								data={data}
								tmpPersoner={tmpPersoner}
								ident={ident}
							/>
						)}
					</DollyFieldArray>
				</ErrorBoundary>
			</div>
		</>
	)
}
