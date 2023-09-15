import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { TelefonData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import * as _ from 'lodash-es'
import VisningRedigerbarSamletConnector from '@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarSamletConnector'

type DataListe = {
	data: Array<TelefonData>
	tmpPersoner: any
	ident: string
	erPdlVisning: boolean
}

type TelefonnummerTypes = {
	telefonnummerData: TelefonData
	idx: number
}

export const TelefonnummerLes = ({ telefonnummerData, idx }: TelefonnummerTypes) => {
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
	if (!data || data.length === 0) {
		return null
	}
	const initialValues = { telefonnummer: data }

	const redigertTelefonnummerPdlf = _.get(tmpPersoner, `${ident}.person.telefonnummer`)
	const redigertTelefonnummerValues = redigertTelefonnummerPdlf && {
		telefonnummer: _.get(tmpPersoner, `${ident}.person.telefonnummer`),
	}

	const slettetTelefonnummerPdlf = tmpPersoner?.hasOwnProperty(ident) && !redigertTelefonnummerPdlf

	const disableSlett = (telefonData: Array<TelefonData>) => {
		if (telefonData.filter((obj) => obj !== null).length < 2) {
			return null
		} else {
			return telefonData.map((tlf) => tlf?.id).indexOf(1)
		}
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
						initialValues={initialValues}
						redigertAttributt={redigertTelefonnummerValues}
						path="telefonnummer"
						ident={ident}
						alleSlettet={slettetTelefonnummerPdlf}
						disableSlett={disableSlett}
					/>
				)}
			</div>
		</div>
	)
}
