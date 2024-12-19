import { AdresseKodeverk } from '@/config/kodeverk'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { oversettBoolean } from '@/utils/DataFormatter'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import React from 'react'
import { getInitialUtenlandskIdentifikasjonsnummer } from '@/components/fagsystem/pdlf/form/initialValues'
import * as _ from 'lodash-es'
import VisningRedigerbarConnector from '@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarConnector'
import { OpplysningSlettet } from '@/components/fagsystem/pdlf/visning/visningRedigerbar/OpplysningSlettet'

const UtenlandsIdLes = ({ data, idx }) => {
	if (!data) {
		return null
	}
	return (
		<div className="person-visning_content" key={idx}>
			<TitleValue title="Identifikasjonsnummer" value={data.identifikasjonsnummer} />
			<TitleValue
				title="Utstederland"
				value={data.utstederland}
				kodeverk={AdresseKodeverk.Utstederland}
			/>
			<TitleValue title="Opphørt" value={oversettBoolean(Boolean(data.opphoert))} />
			<TitleValue title="Master" value={data.master || data?.metadata?.master} />
		</div>
	)
}

export const UtenlandsIdVisning = ({
	utenlandsIdData,
	idx,
	data,
	tmpPersoner,
	ident,
	identtype,
}) => {
	const initUtenlandsId = Object.assign(
		_.cloneDeep(getInitialUtenlandskIdentifikasjonsnummer()),
		data[idx],
	)
	let initialValues = { utenlandskIdentifikasjonsnummer: initUtenlandsId }

	const redigertUtenlandsIdPdlf = _.get(
		tmpPersoner,
		`${ident}.person.utenlandskIdentifikasjonsnummer`,
	)?.find((a) => a.id === utenlandsIdData.id)

	const slettetUtenlandsIdPdlf = tmpPersoner?.hasOwnProperty(ident) && !redigertUtenlandsIdPdlf
	if (slettetUtenlandsIdPdlf) {
		return <OpplysningSlettet />
	}

	const utenlandsIdValues = redigertUtenlandsIdPdlf ? redigertUtenlandsIdPdlf : utenlandsIdData

	let redigertUtenlandsIdValues = redigertUtenlandsIdPdlf && {
		utenlandskIdentifikasjonsnummer: Object.assign(
			_.cloneDeep(initUtenlandsId),
			redigertUtenlandsIdPdlf,
		),
	}

	return (
		<VisningRedigerbarConnector
			dataVisning={<UtenlandsIdLes data={utenlandsIdValues} idx={idx} />}
			initialValues={initialValues}
			redigertAttributt={redigertUtenlandsIdValues}
			path="utenlandskIdentifikasjonsnummer"
			ident={ident}
			identtype={identtype}
		/>
	)
}

export const UtenlandsId = ({ data, tmpPersoner, ident, identtype, erRedigerbar = true }) => {
	if (!data || data.length === 0) {
		return null
	}

	return (
		<div>
			<SubOverskrift label="Utenlandsk identifikasjonsnummer" iconKind="identifikasjon" />
			<ErrorBoundary>
				<DollyFieldArray data={data} nested>
					{(id, idx) =>
						erRedigerbar ? (
							<UtenlandsIdVisning
								utenlandsIdData={id}
								idx={idx}
								data={data}
								tmpPersoner={tmpPersoner}
								ident={ident}
								identtype={identtype}
							/>
						) : (
							<UtenlandsIdLes data={id} idx={idx} />
						)
					}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
