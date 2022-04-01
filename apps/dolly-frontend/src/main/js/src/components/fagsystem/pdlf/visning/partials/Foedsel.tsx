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
import Loading from '~/components/ui/loading/Loading'
import _get from 'lodash/get'
import styled from 'styled-components'
import NavButton from '~/components/ui/button/NavButton/NavButton'

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
	LoadingPdlf = 'LOADING_PDLF',
	LoadingPdl = 'LOADING_PDL',
}

const FieldArrayEdit = styled.div`
	&&& {
		button {
			position: relative;
			top: 0;
			right: 0;
			margin-right: 10px;
		}
	}
`

const Knappegruppe = styled.div`
	margin: 10px 0 5px 0;
	align-content: baseline;
`

export const Foedsel = ({
	data,
	put,
	sendOrdrePdl,
	fetch,
	getPdl,
	tmpPersoner,
	ident,
	erPdlVisning = false,
}: FoedselTypes) => {
	if (!data || data.length === 0) return null

	const [visningModus, setVisningModus] = useState(Modus.Les)
	const [errorMessage, setErrorMessage] = useState(null)

	// const foedselTest = {
	// 	fodekommune: null,
	// 	foedeland: 'LIER',
	// 	foedested: null,
	// 	foedselsaar: 1979,
	// 	foedselsdato: '1979-08-19T00:00:00',
	// 	kilde: 'Dolly',
	// 	master: 'FREG',
	// 	id: 1,
	// }

	const handleSubmit = async (data) => {
		setVisningModus(Modus.LoadingPdlf)
		const attributt = Object.keys(data)[0]
		await put(attributt, data.foedsel.id, data.foedsel)
			.then((putResponse) => {
				if (putResponse)
					fetch().then((fetchResponse) => {
						if (fetchResponse) setVisningModus(Modus.LoadingPdl)
					})
			})
			.catch((error) => {
				fetch()
				setErrorMessage(error.toString())
				setVisningModus(Modus.Les)
			})
			.then(() =>
				sendOrdrePdl().then((sendOrdrePdlResponse) => {
					if (sendOrdrePdlResponse)
						setTimeout(() => {
							getPdl().then((getPdlResponse) => {
								if (getPdlResponse) setVisningModus(Modus.Les)
							})
						}, 5000)
				})
			)
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
			{!erPdlVisning && <Button kind="edit" onClick={() => setVisningModus(Modus.Skriv)} />}
			{/*<Button kind="trashcan" onClick={() => console.log('klikk slett!')} />*/}
		</div>
	)

	const FoedselVisning = ({ item, idx }: FoedselVisningTypes) => {
		const initFoedsel = Object.assign(initialFoedsel, data[idx])
		const initialValues = { foedsel: initFoedsel }

		const redigertFoedselPdlf = _get(tmpPersoner, `${ident}.person.foedsel`)?.find(
			(a) => a.id === item.id
		)
		const redigertFoedselPdl = _get(tmpPersoner, `${ident}.foedsel.${idx}`)

		return (
			<>
				{visningModus === Modus.LoadingPdlf && <Loading label="Oppdaterer PDL-forvalter..." />}
				{visningModus === Modus.LoadingPdl && <Loading label="Oppdaterer PDL..." />}
				{visningModus === Modus.Les && (
					<>
						{redigertFoedselPdlf || redigertFoedselPdl ? (
							<FoedselLes foedsel={redigertFoedselPdlf || redigertFoedselPdl} idx={idx} />
						) : (
							<FoedselLes foedsel={item} idx={idx} />
						)}
						{errorMessage && <div className="error-message">{errorMessage}</div>}
					</>
				)}
				{visningModus === Modus.Skriv && (
					<Formik
						initialValues={redigertFoedselPdlf ? { foedsel: redigertFoedselPdlf } : initialValues}
						onSubmit={handleSubmit}
						enableReinitialize
					>
						{(formikBag) => {
							return (
								<>
									<FieldArrayEdit>
										<div className="flexbox--flex-wrap">
											<FoedselForm formikBag={formikBag} path="foedsel" />
										</div>
										<Knappegruppe>
											<NavButton
												type="standard"
												htmlType="reset"
												onClick={() => setVisningModus(Modus.Les)}
												disabled={!formikBag.isValid || formikBag.isSubmitting}
												style={{ top: '1.75px' }}
											>
												Avbryt
											</NavButton>
											<NavButton
												type="hoved"
												htmlType="submit"
												onClick={() => formikBag.handleSubmit()}
												disabled={!formikBag.isValid || formikBag.isSubmitting}
											>
												Endre
											</NavButton>
										</Knappegruppe>
									</FieldArrayEdit>
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
