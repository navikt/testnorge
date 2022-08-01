import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { TelefonData } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import _cloneDeep from 'lodash/cloneDeep'
import { initialTelefonnummer } from '~/components/fagsystem/pdlf/form/initialValues'
import _get from 'lodash/get'
import VisningRedigerbarConnector from '~/components/fagsystem/pdlf/visning/VisningRedigerbarConnector'
import VisningRedigerbarSamletConnector from '~/components/fagsystem/pdlf/visning/VisningRedigerbarSamletConnector'

type DataListe = {
	data: Array<TelefonData>
}

type Data = {
	item: TelefonData
	idx: number
}

export const TelefonnummerLes = ({ telefonnummerData, idx }) => {
	if (!telefonnummerData) {
		return null
	}

	const telefonnummer = telefonnummerData.nummer || telefonnummerData.telefonnummer
	const landkode = telefonnummerData.landskode || telefonnummerData.landkode
	const prioritet =
		telefonnummerData.prioritet ||
		(telefonnummerData.telefontype === 'MOBI' && 1) ||
		(telefonnummerData.telefontype === 'HJET' && 2)

	return (
		<div className="person-visning_redigerbar" key={idx}>
			<TitleValue title="Telefonnummer" value={`${landkode} ${telefonnummer}`} />
			<TitleValue title="Prioritet" value={prioritet} />
		</div>
	)
}

export const Telefonnummer = ({ data, tmpPersoner, ident, erPdlVisning = false }: DataListe) => {
	if (!data || data.length === 0) return null
	const initialValues = { telefonnummer: data }
	// console.log('tmpPersoner: ', tmpPersoner) //TODO - SLETT MEG

	const redigertTelefonnummerPdlf = _get(tmpPersoner, `${ident}.person.telefonnummer`)
	const redigertTelefonnummerValues = redigertTelefonnummerPdlf && {
		telefonnummer: _get(tmpPersoner, `${ident}.person.telefonnummer`),
	}

	const slettetTelefonnummerPdlf = tmpPersoner?.hasOwnProperty(ident) && !redigertTelefonnummerPdlf
	if (slettetTelefonnummerPdlf) return <pre style={{ margin: '0' }}>Opplysning slettet</pre>

	// const telefonnummerValues = redigertTelefonnummerPdlf ? redigertTelefonnummerPdlf : item
	// const redigertTelefonnummerValues = redigertTelefonnummerPdlf
	// 	? {
	// 		telefonnummer: Object.assign(_cloneDeep(initialTelefonnummer), redigertTelefonnummerPdlf),
	// 	}
	// 	: null

	const TelefonnummerVisning = ({ item, idx }: Data) => {
		// console.log('data: ', data) //TODO - SLETT MEG
		// console.log('item: ', item) //TODO - SLETT MEG
		// const initTelefonnummer = Object.assign(_cloneDeep(initialTelefonnummer), data[idx])
		// const initialValues = { telefonnummer: initTelefonnummer }

		const redigertTelefonnummerPdlf = _get(tmpPersoner, `${ident}.person.telefonnummer`)?.find(
			(a: any) => a.id === item.id
		)
		const slettetTelefonnummerPdlf =
			tmpPersoner?.hasOwnProperty(ident) && !redigertTelefonnummerPdlf
		if (slettetTelefonnummerPdlf) return <pre style={{ margin: '0' }}>Opplysning slettet</pre>

		const telefonnummerValues = redigertTelefonnummerPdlf ? redigertTelefonnummerPdlf : item
		const redigertTelefonnummerValues = redigertTelefonnummerPdlf
			? {
					telefonnummer: Object.assign(_cloneDeep(initialTelefonnummer), redigertTelefonnummerPdlf),
			  }
			: null
		// return erPdlVisning ? (
		// 	<TelefonnummerLes telefonnummerData={telefonnummerValues} idx={idx} />
		// ) : (
		// 	<VisningRedigerbarConnector
		// 		dataVisning={<TelefonnummerLes telefonnummerData={telefonnummerValues} idx={idx} />}
		// 		initialValues={initialValues}
		// 		redigertAttributt={redigertTelefonnummerValues}
		// 		path="telefonnummer"
		// 		ident={ident}
		// 	/>
		// )
		return <TelefonnummerLes telefonnummerData={telefonnummerValues} idx={idx} />
	}

	return (
		<div>
			<SubOverskrift label="Telefonnummer" iconKind="telephone" />
			<div className="person-visning_content">
				{erPdlVisning ? (
					<ErrorBoundary>
						<DollyFieldArray data={data} header="" nested>
							{(item: TelefonData, idx: number) => (
								<TelefonnummerLes telefonnummerData={item} idx={idx} />
							)}
						</DollyFieldArray>
					</ErrorBoundary>
				) : (
					<VisningRedigerbarSamletConnector
						// dataVisning={
						// 	<DollyFieldArray data={data} header="" nested>
						// 		{(item: TelefonData, idx: number) => <TelefonnummerVisning item={item} idx={idx} />}
						// 	</DollyFieldArray>
						// }
						initialValues={initialValues}
						redigertAttributt={redigertTelefonnummerValues}
						path="telefonnummer"
						ident={ident}
					/>
				)}
			</div>
		</div>
	)
}
