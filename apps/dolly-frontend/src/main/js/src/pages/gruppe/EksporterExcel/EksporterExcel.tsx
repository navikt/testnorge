import { useState } from 'react'
import { format } from 'date-fns'
import { Button } from '@navikt/ds-react'
import { FilePlusIcon } from '@navikt/aksel-icons'

export const EksporterExcel = ({
	gruppeId,
	brukerId,
}: {
	gruppeId?: number
	brukerId?: string
}) => {
	const [isLoading, setIsLoading] = useState(false)

	const handleDownload = async () => {
		try {
			setIsLoading(true)
			const response = await fetch(
				gruppeId
					? `/dolly-backend/api/v1/excel/gruppe/${gruppeId}`
					: `/dolly-backend/api/v1/excel/organisasjoner?brukerId=${brukerId}`,
			)
			if (!response.ok) {
				console.error(`Error downloading Excel file.`)
			}
			const blob = await response.blob()
			const url = URL.createObjectURL(blob)
			const date = format(new Date(), 'yyyy-MM-dd')
			const link = document.createElement('a')
			link.href = url
			link.download = `${gruppeId || 'org'}_${date}.xlsx`
			link.target = '_blank'
			document.body.appendChild(link)
			link.click()
			document.body.removeChild(link)
			URL.revokeObjectURL(url)
		} catch (error) {
			setIsLoading(false)
			console.error(error)
		} finally {
			setIsLoading(false)
		}
	}

	return (
		<Button
			size="xsmall"
			variant="tertiary"
			data-color="success"
			icon={<FilePlusIcon aria-hidden />}
			onClick={handleDownload}
			loading={isLoading}
		>
			Eksporter til Excel
		</Button>
	)
}
