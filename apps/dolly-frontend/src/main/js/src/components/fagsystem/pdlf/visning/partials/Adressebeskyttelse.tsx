import React from 'react'
import * as _ from 'lodash-es'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { showLabel } from '@/utils/DataFormatter'
import { getInitialAdressebeskyttelse } from '@/components/fagsystem/pdlf/form/initialValues'
import { AdressebeskyttelseData, Person } from '@/components/fagsystem/pdlf/PdlTypes'
import { VisningRedigerbar } from "@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbar"
import { OpplysningSlettet } from '@/components/fagsystem/pdlf/visning/visningRedigerbar/OpplysningSlettet'

type AdressebeskyttelseTypes = {
	data: Array<AdressebeskyttelseData>
	tmpPersoner?: Array<AdressebeskyttelseData>
	ident?: string
	identtype?: string
	erPdlVisning?: boolean
	erRedigerbar?: boolean
}

type AdressebeskyttelseVisningTypes = {
	adressebeskyttelse: AdressebeskyttelseData
	idx: number
	data: Array<AdressebeskyttelseData>
	tmpPersoner: Array<AdressebeskyttelseData>
	ident?: string
	identtype?: string
	erPdlVisning?: boolean
	master?: string
}

type AdressebeskyttelseLesTypes = {
	adressebeskyttelse: AdressebeskyttelseData
	idx: number
}

const AdressebeskyttelseLes = ({ adressebeskyttelse, idx }: AdressebeskyttelseLesTypes) => {
	return (
		<>
			<div className="person-visning_content" key={idx}>
				<TitleValue
					title="Gradering"
					value={showLabel('gradering', adressebeskyttelse.gradering)}
				/>
				<TitleValue
					title="Master"
					value={adressebeskyttelse.master || adressebeskyttelse.metadata?.master}
				/>
			</div>
		</>
	)
}

const AdressebeskyttelseVisning = ({
	adressebeskyttelse,
	idx,
	data,
	tmpPersoner,
	ident,
	identtype,
	erPdlVisning,
	master,
}: AdressebeskyttelseVisningTypes) => {
	const initAdressebeskyttelse = Object.assign(
		_.cloneDeep(getInitialAdressebeskyttelse()),
		data[idx],
	)
	const initialValues = { adressebeskyttelse: initAdressebeskyttelse }

	const redigertAdressebeskyttelsePdlf = _.get(
		tmpPersoner,
		`${ident}.person.adressebeskyttelse`,
	)?.find((a: Person) => a.id === adressebeskyttelse.id)
	const slettetAdressebeskyttelsePdlf =
		tmpPersoner?.hasOwnProperty(ident) && !redigertAdressebeskyttelsePdlf
	if (slettetAdressebeskyttelsePdlf) {
		return <OpplysningSlettet />
	}

	const adressebeskyttelseValues = redigertAdressebeskyttelsePdlf
		? redigertAdressebeskyttelsePdlf
		: adressebeskyttelse
	const redigertAdressebeskyttelseValues = redigertAdressebeskyttelsePdlf
		? {
				adressebeskyttelse: Object.assign(
					_.cloneDeep(getInitialAdressebeskyttelse()),
					redigertAdressebeskyttelsePdlf,
				),
			}
		: null

	return erPdlVisning ? (
		<AdressebeskyttelseLes adressebeskyttelse={adressebeskyttelse} idx={idx} />
	) : (
		<VisningRedigerbar
			dataVisning={
				<AdressebeskyttelseLes adressebeskyttelse={adressebeskyttelseValues} idx={idx} />
			}
			initialValues={initialValues}
			redigertAttributt={redigertAdressebeskyttelseValues}
			path="adressebeskyttelse"
			ident={ident}
			identtype={identtype}
			master={master}
		/>
	)
}

export const Adressebeskyttelse = ({
	data,
	pdlfData,
	tmpPersoner,
	ident,
	identtype,
	erPdlVisning = false,
	erRedigerbar = true,
}: AdressebeskyttelseTypes) => {
	if (!data || data.length === 0) {
		return null
	}

	return (
		<>
			<SubOverskrift label="Adressebeskyttelse" iconKind="adresse" />
			<div className="person-visning_content">
				<ErrorBoundary>
					<DollyFieldArray data={data} header="" nested>
						{(adressebeskyttelse: AdressebeskyttelseData, idx: number) => {
							const master = adressebeskyttelse?.metadata?.master
							const pdlfElement = pdlfData?.find(
								(item) => item.hendelseId === adressebeskyttelse?.metadata?.opplysningsId,
							)
							if (erRedigerbar) {
								return (
									<AdressebeskyttelseVisning
										adressebeskyttelse={
											master === 'PDL' && pdlfElement ? pdlfElement : adressebeskyttelse
										}
										idx={idx}
										tmpPersoner={tmpPersoner}
										ident={ident}
										identtype={identtype}
										data={master === 'PDL' && pdlfData ? pdlfData : data}
										erPdlVisning={erPdlVisning}
										master={master}
									/>
								)
							}
							return <AdressebeskyttelseLes adressebeskyttelse={adressebeskyttelse} idx={idx} />
						}}
					</DollyFieldArray>
				</ErrorBoundary>
			</div>
		</>
	)
}
