import { format } from 'date-fns'
import Button from '@/components/ui/button/Button'
import Loading from '@/components/ui/loading/Loading'
import './EksporterExcel.less'
import { Logger } from '@/logger/Logger'

const downloadExcelString = (filPrefix, fileString) => {
	const dagensDato = format(new Date(), 'yyyy-MM-dd')
	const link = document.createElement('a')

	link.href = fileString
	link.download = filPrefix + '_' + dagensDato + '.xlsx'
	link.target = '_blank'
	document.body.appendChild(link)
	link.click()
	document.body.removeChild(link)
}

export const EksporterExcel = ({ exportId, filPrefix, action, loading }) => {
	if (loading) return <Loading label="henter fil..." />

	const download = async () => {
		Logger.log({ event: 'Eksporterer til excel' })
		const data = await action(exportId)
		downloadExcelString(filPrefix, data?.value)
	}

	return (
		<Button
			className="flexbox--align-center csv-eksport-btn"
			kind="file-new-table"
			onClick={download}
		>
			EKSPORTER TIL EXCEL
		</Button>
	)
}
