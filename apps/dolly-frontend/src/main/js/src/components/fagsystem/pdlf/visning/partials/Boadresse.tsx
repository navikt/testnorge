import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { Vegadresse } from '~/components/fagsystem/pdlf/visning/partials/Vegadresse'
import { Matrikkeladresse } from '~/components/fagsystem/pdlf/visning/partials/Matrikkeladresse'
import { UtenlandskAdresse } from '~/components/fagsystem/pdlf/visning/partials/UtenlandskAdresse'
import { UkjentBosted } from '~/components/fagsystem/pdlf/visning/partials/UkjentBosted'
import _cloneDeep from 'lodash/cloneDeep'
import { initialBostedsadresse } from '~/components/fagsystem/pdlf/form/initialValues'
import _get from 'lodash/get'
import VisningRedigerbarConnector from '~/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarConnector'
import { BostedData } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'

type BoadresseTypes = {
	data: Array<any>
	tmpPersoner?: Array<BostedData>
	ident?: string
	identtype?: string
	erPdlVisning?: boolean
}

type BoadresseVisningTypes = {
	boadresseData: any
	idx: number
}

export const Adresse = ({ boadresseData, idx }: BoadresseVisningTypes) => {
	if (!boadresseData) {
		return null
	}
	return (
		<>
			{boadresseData.vegadresse && <Vegadresse adresse={boadresseData} idx={idx} />}
			{boadresseData.matrikkeladresse && <Matrikkeladresse adresse={boadresseData} idx={idx} />}
			{boadresseData.utenlandskAdresse && <UtenlandskAdresse adresse={boadresseData} idx={idx} />}
			{boadresseData.ukjentBosted && <UkjentBosted adresse={boadresseData} idx={idx} />}
		</>
	)
}

export const Boadresse = ({
	data,
	tmpPersoner,
	ident,
	identtype,
	erPdlVisning = false,
}: BoadresseTypes) => {
	if (!data || data.length === 0) {
		return null
	}

	const BoadresseVisning = ({ boadresseData, idx }: BoadresseVisningTypes) => {
		const initBoadresse = Object.assign(_cloneDeep(initialBostedsadresse), data[idx])
		const initialValues = { bostedsadresse: initBoadresse }

		const redigertBoadressePdlf = _get(tmpPersoner, `${ident}.person.bostedsadresse`)?.find(
			(a: BostedData) => a.id === boadresseData.id
		)
		const slettetBoadressePdlf = tmpPersoner?.hasOwnProperty(ident) && !redigertBoadressePdlf
		if (slettetBoadressePdlf) {
			return <pre style={{ margin: '0' }}>Opplysning slettet</pre>
		}

		const boadresseValues = redigertBoadressePdlf ? redigertBoadressePdlf : boadresseData
		const redigertBoadresseValues = redigertBoadressePdlf
			? {
					bostedsadresse: Object.assign(_cloneDeep(initialBostedsadresse), redigertBoadressePdlf),
			  }
			: null

		const filtrertData = [...data]
		filtrertData.splice(idx, 1)
		const personFoerLeggTil = {
			pdlforvalter: {
				person: {
					bostedsadresse: filtrertData,
				},
			},
		}
		return erPdlVisning ? (
			<Adresse boadresseData={boadresseData} idx={idx} />
		) : (
			<VisningRedigerbarConnector
				dataVisning={<Adresse boadresseData={boadresseValues} idx={idx} />}
				initialValues={initialValues}
				redigertAttributt={redigertBoadresseValues}
				path="bostedsadresse"
				ident={ident}
				identtype={identtype}
				personFoerLeggTil={personFoerLeggTil}
			/>
		)
	}

	return (
		<>
			<SubOverskrift label="Boadresse" iconKind="adresse" />
			<div className="person-visning_content">
				<ErrorBoundary>
					<DollyFieldArray data={data} header="" nested>
						{(adresse: any, idx: number) => <BoadresseVisning boadresseData={adresse} idx={idx} />}
					</DollyFieldArray>
				</ErrorBoundary>
			</div>
		</>
	)
}
