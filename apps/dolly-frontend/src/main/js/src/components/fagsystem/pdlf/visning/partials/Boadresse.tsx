import React from 'react'
import * as _ from 'lodash-es'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { Vegadresse } from '@/components/fagsystem/pdlf/visning/partials/Vegadresse'
import { Matrikkeladresse } from '@/components/fagsystem/pdlf/visning/partials/Matrikkeladresse'
import { UtenlandskAdresse } from '@/components/fagsystem/pdlf/visning/partials/UtenlandskAdresse'
import { UkjentBosted } from '@/components/fagsystem/pdlf/visning/partials/UkjentBosted'
import { getInitialBostedsadresse } from '@/components/fagsystem/pdlf/form/initialValues'
import VisningRedigerbarConnector from '@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarConnector'
import { BostedData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { OpplysningSlettet } from '@/components/fagsystem/pdlf/visning/visningRedigerbar/OpplysningSlettet'

type BoadresseTypes = {
	data: Array<any>
	tmpPersoner?: Array<BostedData>
	ident?: string
	identtype?: string
	erPdlVisning?: boolean
	erRedigerbar?: boolean
}

type BoadresseVisningTypes = {
	boadresseData: any
	data: Array<any>
	idx: number
	tmpPersoner?: Array<BostedData>
	ident?: string
	identtype?: string
	erPdlVisning?: boolean
	master?: string
}

type AdresseTypes = {
	boadresseData: any
	idx: number
}

export const Adresse = ({ boadresseData, idx }: AdresseTypes) => {
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

export const BoadresseVisning = ({
	boadresseData,
	idx,
	data,
	tmpPersoner,
	ident,
	identtype,
	erPdlVisning,
	master,
}: BoadresseVisningTypes) => {
	const initBoadresse = Object.assign(_.cloneDeep(getInitialBostedsadresse()), data[idx])
	const initialValues = { bostedsadresse: initBoadresse }

	const redigertBoadressePdlf = _.get(tmpPersoner, `${ident}.person.bostedsadresse`)?.find(
		(a: BostedData) => a.id === boadresseData.id,
	)
	const slettetBoadressePdlf = tmpPersoner?.hasOwnProperty(ident) && !redigertBoadressePdlf
	if (slettetBoadressePdlf) {
		return <OpplysningSlettet />
	}

	const boadresseValues = redigertBoadressePdlf ? redigertBoadressePdlf : boadresseData
	const redigertBoadresseValues = redigertBoadressePdlf
		? {
				bostedsadresse: Object.assign(
					_.cloneDeep(getInitialBostedsadresse()),
					redigertBoadressePdlf,
				),
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
			master={master}
		/>
	)
}

export const Boadresse = ({
	data,
	tmpPersoner,
	ident,
	identtype,
	erPdlVisning = false,
	erRedigerbar = true,
}: BoadresseTypes) => {
	if (!data || data.length === 0) {
		return null
	}

	return (
		<>
			<SubOverskrift label="Boadresse" iconKind="adresse" />
			<div className="person-visning_content">
				<ErrorBoundary>
					<DollyFieldArray data={data} header="" nested>
						{(adresse: any, idx: number) =>
							erRedigerbar ? (
								<BoadresseVisning
									boadresseData={adresse}
									idx={idx}
									data={data}
									tmpPersoner={tmpPersoner}
									identtype={identtype}
									ident={ident}
									erPdlVisning={erPdlVisning}
								/>
							) : (
								<Adresse boadresseData={adresse} idx={idx} />
							)
						}
					</DollyFieldArray>
				</ErrorBoundary>
			</div>
		</>
	)
}
