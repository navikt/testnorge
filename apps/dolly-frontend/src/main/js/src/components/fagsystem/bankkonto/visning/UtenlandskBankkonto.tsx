import React, { useCallback } from 'react'
import { ArbeidKodeverk, GtKodeverk } from '@/config/kodeverk'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import Button from '@/components/ui/button/Button'
import useBoolean from '@/utils/hooks/useBoolean'
import styled from 'styled-components'
import Icon from '@/components/ui/icon/Icon'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import { DollyModal } from '@/components/ui/modal/DollyModal'
import { BankkontoApi, TpsMessagingApi } from '@/service/Api'
import { formatDate } from '@/utils/DataFormatter'

const EditDeleteKnapper = styled.div`
	position: absolute;
	right: 0;
	top: 0;
	margin: -5px 10px 0 0;

	&&& {
		button {
			position: relative;
		}
	}
`

type Data = {
	data: UtenlandskBankkontoData
	extraButtons?: boolean
	ident?: string
}

type UtenlandskBankkontoData = {
	kontonummer: string
	gyldig: string
	tilfeldigKontonummer: boolean
	swift: string
	landkode: string
	banknavn?: string
	bankkode?: string
	iban: string
	valuta: string
	bankAdresse1?: string
	bankAdresse2?: string
	bankAdresse3?: string
}

export const Visning = ({ data, extraButtons, ident }: Data) => {
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)
	const [show, , setHide] = useBoolean(true)

	const handleDelete = useCallback(() => {
		const slett = async () => {
			const kontoregister = BankkontoApi.slettKonto(ident)
			const tpsMessaging = TpsMessagingApi.deleteBankkontoUtenlandsk(ident)
			await Promise.all([kontoregister, tpsMessaging]).catch((e) => console.error(e))
			setHide()
		}
		if (ident) {
			return slett()
		} else {
			return
		}
	}, [])

	if (!show) {
		return null
	}

	return (
		<div style={{ position: 'relative' }}>
			<div className="person-visning_content">
				<ErrorBoundary>
					<TitleValue title={'Kontonummer'} value={data.kontonummer} />
					<TitleValue
						title={'TilfeldigKontonummer'}
						value={data.tilfeldigKontonummer ? 'Ja' : ''}
					/>
					<TitleValue title={'Gyldig f.o.m.'} value={formatDate(data.gyldig)} />
					<TitleValue title={'BIC/SWIFT-kode'} value={data.swift} />
					<TitleValue title={'Land'} kodeverk={GtKodeverk.LAND} value={data.landkode} />
					<TitleValue title={'Banknavn'} value={data.banknavn} />
					<TitleValue title={'Bankkode'} value={data.bankkode} />
					<TitleValue title={'Valuta'} kodeverk={ArbeidKodeverk.Valutaer} value={data.valuta} />
					<TitleValue title={'Adresselinje 1'} value={data.bankAdresse1} />
					<TitleValue title={'Adresselinje 2'} value={data.bankAdresse2} />
					<TitleValue title={'Adresselinje 3'} value={data.bankAdresse3} />
				</ErrorBoundary>
			</div>
			{extraButtons && (
				<EditDeleteKnapper>
					<Button kind="trashcan" onClick={() => openModal()} title="Slett" />
					<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width="40%" overflow="auto">
						<div className="slettModal">
							<div className="slettModal slettModal-content">
								<Icon size={50} kind="report-problem-circle" />
								<h1>Slett</h1>
								<h4>Er du sikker på at du vil slette denne bankkontoen fra personen?</h4>
							</div>
							<div className="slettModal-actions">
								<NavButton onClick={closeModal} variant={'secondary'}>
									Nei
								</NavButton>
								<NavButton
									onClick={() => {
										closeModal()
										return handleDelete()
									}}
									variant={'primary'}
								>
									Ja, jeg er sikker
								</NavButton>
							</div>
						</div>
					</DollyModal>
				</EditDeleteKnapper>
			)}
		</div>
	)
}

export const UtenlandskBankkonto = ({ data, ident, extraButtons = null }: Data) => {
	if (!data) {
		return null
	}
	return (
		<div>
			<SubOverskrift label="Utenlandsk bankkonto" iconKind="bankkonto" />
			<Visning data={data} extraButtons={extraButtons} ident={ident} />
		</div>
	)
}
