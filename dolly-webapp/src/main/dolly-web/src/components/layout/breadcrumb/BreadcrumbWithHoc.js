import withBreadcrumbs from 'react-router-breadcrumbs-hoc'
import routes from '~/Routes'
import BreadCrumb from './Breadcrumb'

export default withBreadcrumbs(routes, { disableDefaults: true })(BreadCrumb)
