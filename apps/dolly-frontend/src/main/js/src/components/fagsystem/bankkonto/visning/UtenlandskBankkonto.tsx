import React, { useCallback } from 'react'
import { ArbeidKodeverk, GtKodeverk } from '@/config/kodeverk'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import useBoolean from '@/utils/hooks/useBoolean'
import styled from 'styled-components'
import { BankkontoApi, TpsMessagingApi } from '@/service/Api'
import { formatDate } from '@/utils/DataFormatter'
import { BekreftSlettDialog } from '@/components/ui/button/SlettDialog/BekreftSlettDialog'

const EditDeleteKnapper = styled.div`
	position: absolute;
	right: 8px;
	top: 0;
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
					<BekreftSlettDialog
						tittel="Er du sikker på at du vil slette bankkontoen?"
						beskrivelse="Du er i ferd med å slette en bankkonto fra personen. Denne handlingen kan ikke angres."
						onBekreft={handleDelete}
					/>
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
