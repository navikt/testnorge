import React, { useState } from 'react'
import { format } from 'date-fns'
import Button from '~/components/ui/button/Button'
import Logger from '~/logger'
import api from '@/api'
import config from '~/config'

import './EksporterCSV.less'

const downloadCsvString = (gruppeId, csvString) => {
	const href = 'data:text/csv,\uFEFF' //uFEFF fikser æøå
	const dagensDato = format(new Date(), 'yyyy-MM-dd')
	const link = document.createElement('a')

	//Lager en link til nedlasting som aktiveres uten klikk
	link.href = href + csvString
	link.download = gruppeId + '_' + dagensDato + '.csv'
	link.target = '_blank'
	document.body.appendChild(link)
	link.click()
	document.body.removeChild(link)
}

export const EksporterCSV = ({ identer, gruppeId }) => {
	const [isLoading, setLoading] = useState(false)

	if (!identer) return false

	const download = async () => {
		setLoading(true)
		Logger.log({ event: 'Eksporterer til excel' })
		const identListe = identer.map(ident => ident.ident)
		const data = await hentCsvFil(identListe)

		const decodedData = decodeURIComponent(escape(data))

		downloadCsvString(gruppeId, decodedData)
		setLoading(false)
	}

	return (
		<Button
			className="flexbox--align-center csv-eksport-btn"
			kind="file-new-table"
			loading={isLoading}
			onClick={download}
		>
			EKSPORTER TIL EXCEL
		</Button>
	)
}

const getTpsfUrl = () => `${config.services.proxyBackend}/tpsf`

const hentCsvFil = body => {
	return api
		.fetch(
			`${getTpsfUrl()}/dolly/testdata/excel`,
			{ method: 'POST', headers: { 'Content-Type': 'application/json' } },
			body
		)
		.then(response => response.text())
}
