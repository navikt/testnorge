import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import _cloneDeep from 'lodash/cloneDeep'
import { initialAdressebeskyttelse } from '~/components/fagsystem/pdlf/form/initialValues'
import _get from 'lodash/get'
import { AdressebeskyttelseData, Person } from '~/components/fagsystem/pdlf/PdlTypes'
import VisningRedigerbarConnector from '~/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarConnector'

type AdressebeskyttelseTypes = {
	data: Array<AdressebeskyttelseData>
	tmpPersoner?: Array<AdressebeskyttelseData>
	ident?: string
	identtype?: string
	erPdlVisning?: boolean
}

type AdressebeskyttelseVisningTypes = {
	adressebeskyttelse: AdressebeskyttelseData
	idx: number
}

export const Adressebeskyttelse = ({
	data,
	tmpPersoner,
	ident,
	identtype,
	erPdlVisning = false,
}: AdressebeskyttelseTypes) => {
	if (!data || data.length === 0) {
		return null
	}

	const AdressebeskyttelseLes = ({ adressebeskyttelse, idx }: AdressebeskyttelseVisningTypes) => {
		return (
			<>
				<div className="person-visning_content" key={idx}>
					<TitleValue
						title="Gradering"
						value={Formatters.showLabel('gradering', adressebeskyttelse.gradering)}
					/>
				</div>
			</>
		)
	}

	const AdressebeskyttelseVisning = ({
		adressebeskyttelse,
		idx,
	}: AdressebeskyttelseVisningTypes) => {
		const initAdressebeskyttelse = Object.assign(_cloneDeep(initialAdressebeskyttelse), data[idx])
		const initialValues = { adressebeskyttelse: initAdressebeskyttelse }

		const redigertAdressebeskyttelsePdlf = _get(
			tmpPersoner,
			`${ident}.person.adressebeskyttelse`
		)?.find((a: Person) => a.id === adressebeskyttelse.id)
		const slettetAdressebeskyttelsePdlf =
			tmpPersoner?.hasOwnProperty(ident) && !redigertAdressebeskyttelsePdlf
		if (slettetAdressebeskyttelsePdlf) {
			return <pre style={{ margin: '0' }}>Opplysning slettet</pre>
		}

		const adressebeskyttelseValues = redigertAdressebeskyttelsePdlf
			? redigertAdressebeskyttelsePdlf
			: adressebeskyttelse
		const redigertAdressebeskyttelseValues = redigertAdressebeskyttelsePdlf
			? {
					adressebeskyttelse: Object.assign(
						_cloneDeep(initialAdressebeskyttelse),
						redigertAdressebeskyttelsePdlf
					),
			  }
			: null

		return erPdlVisning ? (
			<AdressebeskyttelseLes adressebeskyttelse={adressebeskyttelse} idx={idx} />
		) : (
			<VisningRedigerbarConnector
				dataVisning={
					<AdressebeskyttelseLes adressebeskyttelse={adressebeskyttelseValues} idx={idx} />
				}
				initialValues={initialValues}
				redigertAttributt={redigertAdressebeskyttelseValues}
				path="adressebeskyttelse"
				ident={ident}
				identtype={identtype}
			/>
		)
	}

	return (
		<>
			<SubOverskrift label="Adressebeskyttelse" iconKind="adresse" />
			<div className="person-visning_content">
				<ErrorBoundary>
					<DollyFieldArray data={data} header="" nested>
						{(adressebeskyttelse: AdressebeskyttelseData, idx: number) => (
							<AdressebeskyttelseVisning adressebeskyttelse={adressebeskyttelse} idx={idx} />
						)}
					</DollyFieldArray>
				</ErrorBoundary>
			</div>
		</>
	)
}
