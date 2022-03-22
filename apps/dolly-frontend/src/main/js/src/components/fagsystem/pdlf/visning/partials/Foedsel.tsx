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
import Loading from '~/components/ui/loading/Loading'
import _get from 'lodash/get'
import styled from 'styled-components'
import { useAsync } from 'react-use'

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
	Loading = 'LOADING',
}

const EditFormButtons = styled(ModalActionKnapper)`
	position: relative;
	.dollymodal {
		background-color: red;

		&_buttons {
			margin: 20px;

			.button {
				position: relative;
			}
		}
	}
`

export const Foedsel = ({ data, put, fetch, tmpPersoner, ident }: FoedselTypes) => {
	if (!data || data.length === 0) return null
	const [visningModus, setVisningModus] = useState(Modus.Les)
	const [errorMessage, setErrorMessage] = useState(null)
	console.log('errorMessage: ', errorMessage) //TODO - SLETT MEG
	const initFoedsel = Object.assign(initialFoedsel, data[0]) //TODO hent fra riktig objekt
	const initialValues = { foedsel: initFoedsel }

	// const handleSubmit = useAsync(async (data) => {
	// 	if (!data) return
	// 	setVisningModus(Modus.Loading)
	// 	const attributt = Object.keys(data)[0]
	// 	await put(attributt, data.foedsel.id, data.foedsel)
	// 	await fetch()
	// 	setVisningModus(Modus.Les)
	// }, [])

	const handleSubmit = async (data) => {
		setVisningModus(Modus.Loading)
		const attributt = Object.keys(data)[0]
		// await put(attributt, data.foedsel.id, data.foedsel)
		await put(attributt, data.foedsel.id, 'xxx')
			.then((response) => {
				console.log('response: ', response) //TODO - SLETT MEG
				if (response) fetch()
				setVisningModus(Modus.Les)
			})
			.catch((error) => {
				console.log('error: ', error) //TODO - SLETT MEG
				fetch()
				setErrorMessage(error.toString())
				setVisningModus(Modus.Les)
			})
		// console.log('test: ', test) //TODO - SLETT MEG
		// if (test) await fetch()
	}

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
		const redigertFoedsel = _get(tmpPersoner, `${ident}.person.foedsel`)?.find(
			(a) => a.id === item.id
		)
		return (
			<>
				{visningModus === Modus.Loading && <Loading label="Endrer....." />}
				{visningModus === Modus.Les && (
					<>
						{redigertFoedsel ? (
							<FoedselLes foedsel={redigertFoedsel} idx={idx} />
						) : (
							<FoedselLes foedsel={item} idx={idx} />
						)}
						{errorMessage && <div className="error-message">{errorMessage}</div>}
					</>
				)}
				{visningModus === Modus.Skriv && (
					<Formik
						initialValues={redigertFoedsel ? { foedsel: redigertFoedsel } : initialValues}
						onSubmit={handleSubmit}
						enableReinitialize
					>
						{(formikBag) => {
							return (
								<>
									<div className="flexbox--full-width">
										<div className="flexbox--flex-wrap">
											<FoedselForm formikBag={formikBag} path="foedsel" />
										</div>
										{/*<ModalActionKnapper*/}
										{/*	submitknapp="Endre"*/}
										{/*	disabled={!formikBag.isValid || formikBag.isSubmitting}*/}
										{/*	onSubmit={() => formikBag.handleSubmit()}*/}
										{/*	onAvbryt={() => setVisningModus(Modus.Les)}*/}
										{/*/>*/}
										<EditFormButtons
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
