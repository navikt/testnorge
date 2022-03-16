import React, { useState } from 'react'
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

type FoedselTypes = {
	data: Array<FoedselData>
}

type FoedselVisningTypes = {
	item: FoedselData
	idx: number
}

enum Modus {
	Les = 'LES',
	Skriv = 'SKRIV',
}

export const Foedsel = ({ data, put, fetch }: FoedselTypes) => {
	if (!data || data.length === 0) return null
	const [visningModus, setVisningModus] = useState(Modus.Les)

	const initFoedsel = Object.assign(initialFoedsel, data[0]) //TODO hent fra riktig objekt
	const initialValues = { foedsel: initFoedsel }

	const handleSubmit = async (data) => {
		console.log('data xxxxxxxx: ', data) //TODO - SLETT MEG
		const attributt = Object.keys(data)[0]
		await put(attributt, data.foedsel.id, data.foedsel)
		// const status = await put(attributt, data.foedsel.id, data.foedsel)
		// console.log('status: ', status) //TODO - SLETT MEG
		await fetch()
		setVisningModus(Modus.Les)
	}

	const FoedselVisning = ({ item, idx }: FoedselVisningTypes) => {
		return (
			<>
				{visningModus === Modus.Les && (
					<div className="person-visning_content" key={idx}>
						<TitleValue title="Fødselsdato" value={Formatters.formatDate(item.foedselsdato)} />
						<TitleValue title="Fødselsår" value={item.foedselsaar} />
						<TitleValue title="Fødested" value={item.foedested} />
						<TitleValue title="Fødekommune">
							{item.fodekommune && (
								<KodeverkConnector navn="Kommuner" value={item.fodekommune}>
									{(v: Kodeverk, verdi: KodeverkValues) => (
										<span>{verdi ? verdi.label : item.fodekommune}</span>
									)}
								</KodeverkConnector>
							)}
						</TitleValue>
						<TitleValue
							title="Fødeland"
							value={item.foedeland}
							kodeverk={AdresseKodeverk.StatsborgerskapLand}
						/>
						<Button kind="edit" onClick={() => setVisningModus(Modus.Skriv)} />
						{/*<Button kind="trashcan" onClick={() => console.log('klikk slett!')} />*/}
					</div>
				)}
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
