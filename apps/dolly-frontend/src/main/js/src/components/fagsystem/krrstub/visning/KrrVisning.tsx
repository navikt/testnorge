import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, oversettBoolean, showLabel } from '@/utils/DataFormatter'
import Loading from '@/components/ui/loading/Loading'
import { KrrApi } from '@/service/Api'
import { DollyModal } from '@/components/ui/modal/DollyModal'
import LoadableComponent from '@/components/ui/loading/LoadableComponent'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { ArrayHistorikk } from '@/components/ui/historikk/ArrayHistorikk'
import Button from '@/components/ui/button/Button'
import Icon from '@/components/ui/icon/Icon'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import styled from 'styled-components'
import useBoolean from '@/utils/hooks/useBoolean'
import { Alert } from '@navikt/ds-react'

const EditDeleteKnapper = styled.div`
	position: absolute;
	right: 8px;
	margin-top: -10px;

	&&& {
		button {
			position: relative;
		}
	}
`

type KrrVisningProps = {
	data: Array<Data>
	loading: boolean
}

type VisningProps = {
	data: Data
}

export type Data = {
	sdpLeverandoer: number
	registrert: boolean
	reservert: boolean
	epost: string
	mobil: string
	landkode: string
	spraak: string
	gyldigFra: string
	sdpAdresse: string
	id?: string
}

type SdpLeverandoer = {
	data: {
		navn: string
	}
}

export const Visning = ({ data }: VisningProps) => {
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)
	const [deleted, setDeleted] = useBoolean(false)
	const disableSlett = !data.id

	const handleDelete = (data: Data) =>
		KrrApi.slettKontaktinformasjon(data.id).then(() => setDeleted())

	if (deleted) {
		return null
	}

	return (
		<>
			<LoadableComponent
				onFetch={() =>
					data.sdpLeverandoer
						? KrrApi.getSdpLeverandoer(data.sdpLeverandoer).then((leverandoer: SdpLeverandoer) => {
								return leverandoer.data
							})
						: Promise.resolve()
				}
				render={(response) =>
					data && (
						<>
							<TitleValue title="Gyldig fra" value={formatDate(data.gyldigFra)} />
							<TitleValue title="Registrert i KRR" value={oversettBoolean(data.registrert)} />
							<TitleValue
								title="Reservert mot digitalkommunikasjon"
								value={oversettBoolean(data.reservert)}
							/>
							<TitleValue title="E-post" value={data.epost} />
							<TitleValue title="Mobilnummer" value={data.mobil} />
							<TitleValue
								title="Språk"
								value={
									data.spraak && showLabel('spraaktype', data.spraak.toLowerCase().replace(' ', ''))
								}
							/>
							<TitleValue title="SDP Adresse" value={data.sdpAdresse} />
							<TitleValue
								title="SDP Leverandør"
								value={response ? response.navn : data.sdpLeverandoer}
							/>

							<EditDeleteKnapper>
								<Button
									kind="trashcan"
									onClick={() => openModal()}
									title="Slett"
									disabled={disableSlett}
								/>
								<DollyModal
									isOpen={modalIsOpen}
									closeModal={closeModal}
									width="40%"
									overflow="auto"
								>
									<div className="slettModal">
										<div className="slettModal slettModal-content">
											<Icon size={50} kind="report-problem-circle" />
											<h1>Sletting</h1>
											<h4>
												Er du sikker på at du vil slette denne kontaktinformasjon fra personen?
											</h4>
										</div>
										<div className="slettModal-actions">
											<NavButton onClick={closeModal} variant={'secondary'}>
												Nei
											</NavButton>
											<NavButton
												onClick={() => {
													closeModal()
													return handleDelete(data)
												}}
												variant={'primary'}
											>
												Ja, jeg er sikker
											</NavButton>
										</div>
									</div>
								</DollyModal>
							</EditDeleteKnapper>
						</>
					)
				}
				label="Laster KRR data"
			/>
		</>
	)
}

export const KrrVisning = ({ data, loading }: KrrVisningProps) => {
	if (loading) {
		return <Loading label="Laster KRR data" />
	}
	if (!data) {
		return null
	}

	const sortedData = Array.isArray(data) ? data?.slice().reverse() : data

	const antallKrr = sortedData?.length
	const antallFremtidige = sortedData?.filter(
		(krr) => krr.gyldigFra && new Date(krr.gyldigFra) > new Date(),
	).length

	const gyldigeData =
		antallKrr > antallFremtidige ? sortedData?.slice(0, antallFremtidige + 1) : sortedData
	const historiskeData =
		antallKrr > gyldigeData?.length ? sortedData?.slice(gyldigeData?.length) : []

	const manglerFagsystemdata = sortedData?.length < 1

	return (
		<ErrorBoundary>
			<div>
				<SubOverskrift
					label="Kontaktinformasjon og reservasjon"
					iconKind="krr"
					isWarning={manglerFagsystemdata}
				/>
				{manglerFagsystemdata ? (
					<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
						Fant ikke KRR-data på person
					</Alert>
				) : (
					<div className="person-visning_content">
						<ArrayHistorikk
							component={Visning}
							data={gyldigeData}
							historiskData={historiskeData}
							header={''}
						/>
					</div>
				)}
			</div>
		</ErrorBoundary>
	)
}
