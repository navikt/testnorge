import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { Vegadresse } from '@/components/fagsystem/pdlf/visning/partials/Vegadresse'
import { Matrikkeladresse } from '@/components/fagsystem/pdlf/visning/partials/Matrikkeladresse'
import { UkjentBosted } from '@/components/fagsystem/pdlf/visning/partials/UkjentBosted'
import { DeltBostedValues, PersonData } from '@/components/fagsystem/pdlf/PdlTypes'
import { initialDeltBosted } from '@/components/fagsystem/pdlf/form/initialValues'
import * as _ from 'lodash-es'
import VisningRedigerbarConnector from '@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarConnector'
import { OpplysningSlettet } from '@/components/fagsystem/pdlf/visning/visningRedigerbar/OpplysningSlettet'

type Data = {
	data: Array<any>
	tmpPersoner?: Array<PersonData>
	ident?: string
	erRedigerbar?: boolean
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

export const DeltBostedVisning = ({
	adresseData,
	idx,
	data,
	tmpPersoner,
	ident,
	personValues,
	relasjoner,
}) => {
	const initBosted = Object.assign(_.cloneDeep(initialDeltBosted), data[idx])
	let initialValues = { deltBosted: initBosted }

	_.set(initialValues, 'deltBosted.adresseIdentifikatorFraMatrikkelen', undefined)

	const redigertBostedPdlf = _.get(tmpPersoner, `${ident}.person.deltBosted`)?.find(
		(a: DeltBostedValues) => a.id === adresseData.id,
	)
	const redigertRelatertePersoner = _.get(tmpPersoner, `${ident}.relasjoner`)

	const slettetBostedtPdlf = tmpPersoner?.hasOwnProperty(ident) && !redigertBostedPdlf
	if (slettetBostedtPdlf) {
		return <OpplysningSlettet />
	}

	const bostedValues = redigertBostedPdlf ? redigertBostedPdlf : adresseData
	let redigertBostedValues = redigertBostedPdlf
		? {
				deltBosted: Object.assign(_.cloneDeep(initialDeltBosted), redigertBostedPdlf),
			}
		: null

	if (redigertBostedValues) {
		_.set(redigertBostedValues, 'deltBosted.adresseIdentifikatorFraMatrikkelen', undefined)
	}

	let personValuesMedRedigert = _.cloneDeep(personValues)
	if (redigertBostedPdlf && personValuesMedRedigert) {
		personValuesMedRedigert.deltBosted = redigertBostedPdlf
	}

	const redigertForelderBarnRelasjon = _.get(tmpPersoner, `${ident}.person.forelderBarnRelasjon`)
	if (redigertForelderBarnRelasjon && personValuesMedRedigert) {
		personValuesMedRedigert.forelderBarnRelasjon = redigertForelderBarnRelasjon
	}

	return (
		<VisningRedigerbarConnector
			dataVisning={<Adresse adresse={bostedValues} idx={idx} />}
			initialValues={initialValues}
			redigertAttributt={redigertBostedValues}
			path="deltBosted"
			ident={ident}
			personValues={personValuesMedRedigert}
			relasjoner={redigertRelatertePersoner || relasjoner}
		/>
	)
}

export const DeltBosted = ({
	data,
	tmpPersoner,
	ident,
	personValues,
	relasjoner,
	erRedigerbar = true,
}: Data) => {
	if (!data || data.length === 0) {
		return null
	}

	return (
		<>
			<SubOverskrift label="Delt bosted" iconKind="adresse" />
			<div className="person-visning_content">
				<ErrorBoundary>
					<DollyFieldArray data={data} header="" nested>
						{(adresse: any, idx: number) =>
							erRedigerbar ? (
								<DeltBostedVisning
									adresseData={adresse}
									idx={idx}
									data={data}
									tmpPersoner={tmpPersoner}
									ident={ident}
									personValues={personValues}
									relasjoner={relasjoner}
								/>
							) : (
								<Adresse adresse={adresse} idx={idx} />
							)
						}
					</DollyFieldArray>
				</ErrorBoundary>
			</div>
		</>
	)
}
