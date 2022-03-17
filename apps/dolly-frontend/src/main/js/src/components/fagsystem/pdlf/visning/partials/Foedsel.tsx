import React, { useEffect, useState } from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import Formatters from '~/utils/DataFormatter'
import KodeverkConnector from '~/components/kodeverk/KodeverkConnector'
import {
	Kodeverk,
	KodeverkValues,
} from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FoedselData } from '~/components/fagsystem/pdlf/PdlTypes'
import Button from '~/components/ui/button/Button'
import { FoedselForm } from '~/components/fagsystem/pdlf/form/partials/foedsel/Foedsel'
import { Formik } from 'formik'
import { initialFoedsel } from '~/components/fagsystem/pdlf/form/initialValues'
import ModalActionKnapper from '~/components/ui/modal/ModalActionKnapper'
import { PdlforvalterApi } from '~/service/Api'
import Loading from '~/components/ui/loading/Loading'

type FoedselTypes = {
	data: Array<FoedselData>
}

type FoedselVisningTypes = {
	item: FoedselData
	idx: number
}

enum Modus {
	Les = 'LES',
	LesEndret = 'LES_ENDRET',
	Skriv = 'SKRIV',
	Loading = 'LOADING',
}

export const Foedsel = ({ data, put, fetch }: FoedselTypes) => {
	if (!data || data.length === 0) return null
	const [visningModus, setVisningModus] = useState(Modus.Les)
	const [endretFoedsel, setEndretFoedsel] = useState(null)

	const initFoedsel = Object.assign(initialFoedsel, data[0]) //TODO hent fra riktig objekt
	const initialValues = endretFoedsel ? { foedsel: endretFoedsel } : { foedsel: initFoedsel }

	// useEffect(() => {}, [endretFoedsel])

	const handleSubmit = async (data) => {
		setVisningModus(Modus.Loading)
		// console.log('data xxxxxxxx: ', data) //TODO - SLETT MEG

		const attributt = Object.keys(data)[0]
		await put(attributt, data.foedsel.id, data.foedsel)
		// await fetch()
		const hentPerson = await PdlforvalterApi.getPersoner(['19087908022'])
		console.log('hentPerson: ', hentPerson) //TODO - SLETT MEG
		await setEndretFoedsel(hentPerson.data[0].person.foedsel[0])
		setVisningModus(Modus.LesEndret)
	}

	console.log('endretFoedsel: ', endretFoedsel) //TODO - SLETT MEG
	console.log('initFoedsel: ', initFoedsel) //TODO - SLETT MEG
	console.log('initialValues: ', initialValues) //TODO - SLETT MEG

	const FoedselLes = ({ foedsel, idx }) => (
		<div className="person-visning_content" key={idx}>
			<TitleValue title="Fødselsdato" value={Formatters.formatDate(foedsel.foedselsdato)} />
			<TitleValue title="Fødselsår" value={foedsel.foedselsaar} />
			<TitleValue title="Fødested" value={foedsel.foedested} />
			<TitleValue title="Fødekommune">
				{foedsel.fodekommune && (
					<KodeverkConnector navn="Kommuner" value={foedsel.fodekommune}>
						{(v: Kodeverk, verdi: KodeverkValues) => (
							<span>{verdi ? verdi.label : foedsel.fodekommune}</span>
						)}
					</KodeverkConnector>
				)}
			</TitleValue>
			<TitleValue
				title="Fødeland"
				value={foedsel.foedeland}
				kodeverk={AdresseKodeverk.StatsborgerskapLand}
			/>
			<Button kind="edit" onClick={() => setVisningModus(Modus.Skriv)} />
			{/*<Button kind="trashcan" onClick={() => console.log('klikk slett!')} />*/}
		</div>
	)

	const FoedselVisning = ({ item, idx }: FoedselVisningTypes) => {
		return (
			<>
				{visningModus === Modus.Loading && <Loading label="Endrer....." />}
				{visningModus === Modus.LesEndret && endretFoedsel && (
					<FoedselLes foedsel={endretFoedsel} idx={idx} />
				)}
				{visningModus === Modus.Les && <FoedselLes foedsel={item} idx={idx} />}
				{visningModus === Modus.Skriv && (
					<Formik initialValues={initialValues} onSubmit={handleSubmit} enableReinitialize>
						{(formikBag) => {
							return (
								<>
									<div className="flexbox--full-width">
										<div className="flexbox--flex-wrap">
											<FoedselForm formikBag={formikBag} path="foedsel" />
										</div>
										<ModalActionKnapper
											submitknapp="Endre"
											disabled={!formikBag.isValid || formikBag.isSubmitting}
											onSubmit={() => formikBag.handleSubmit()}
											onAvbryt={() => setVisningModus(Modus.Les)}
										/>
									</div>
								</>
							)
						}}
					</Formik>
				)}
			</>
		)
	}

	return (
		<div>
			<SubOverskrift label="Fødsel" iconKind="foedsel" />
			<div className="person-visning_content">
				<ErrorBoundary>
					<DollyFieldArray data={data} header="" nested>
						{(item: FoedselData, idx: number) => <FoedselVisning item={item} idx={idx} />}
					</DollyFieldArray>
				</ErrorBoundary>
			</div>
		</div>
	)
}
