// 教学管理系统前端主文件
new Vue({
    el: '#app',
    data() {
        return {
            // 登录相关
            isLoggedIn: false,
            loginForm: {
                username: '',
                password: ''
            },
            loginRules: {
                username: [
                    { required: true, message: '请输入用户名', trigger: 'blur' }
                ],
                password: [
                    { required: true, message: '请输入密码', trigger: 'blur' }
                ]
            },
            currentUser: {
                username: '未登录',
                role: ''
            },
            
            // 导航相关
            activeModule: 'course-management',
            
            // 课程管理模块
            courseManagementFilters: {
                majorId: '',
                courseName: ''
            },
            coursesList: [],
            addCourseDialogVisible: false,
            editCourseDialogVisible: false,
            courseForm: {
                courseId: null,
                courseName: '',
                majorId: '',
                credits: 3.0,
                totalHours: 48,
                courseType: '',
                courseCode: '',
                theoryHours: 0,
                practiceHours: 0,
                courseNature: '',
                courseStatus: 'enabled'
            },
            courseRules: {
                courseName: [
                    { required: true, message: '请输入课程名称', trigger: 'blur' },
                    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
                ],
                majorId: [
                    // 移除必填验证，因为后端Course实体不需要这个字段
                ],
                credits: [
                    { required: true, message: '请输入学分', trigger: 'blur' },
                    { type: 'number', message: '学分必须为数字', trigger: 'blur' }
                ],
                totalHours: [
                    { required: true, message: '请输入总学时', trigger: 'blur' },
                    { type: 'number', message: '总学时必须为数字', trigger: 'blur' }
                ],
                courseType: [
                    { required: true, message: '请选择课程类型', trigger: 'change' }
                ],
                courseCode: [
                    { required: true, message: '请输入课程代码', trigger: 'blur' },
                    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
                ],
                theoryHours: [
                    { required: true, message: '请输入理论学时', trigger: ['blur', 'change'] },
                    { validator: (rule, value, callback) => {
                        // 处理空字符串、null、undefined等情况
                        if (value === null || value === undefined || value === '') {
                            callback(new Error('请输入理论学时'));
                            return;
                        }
                        
                        // 强制转换为整数
                        const num = parseInt(value, 10);
                        
                        if (isNaN(num)) {
                            callback(new Error('理论学时必须为数字'));
                        } else if (num < 0) {
                            callback(new Error('理论学时不能为负数'));
                        } else {
                            callback();
                        }
                    }, trigger: ['blur', 'change'] }
                ],
                practiceHours: [
                    { required: true, message: '请输入实践学时', trigger: ['blur', 'change'] },
                    { validator: (rule, value, callback) => {
                        // 处理空字符串、null、undefined等情况
                        if (value === null || value === undefined || value === '') {
                            callback(new Error('请输入实践学时'));
                            return;
                        }
                        
                        // 强制转换为整数
                        const num = parseInt(value, 10);
                        
                        if (isNaN(num)) {
                            callback(new Error('实践学时必须为数字'));
                        } else if (num < 0) {
                            callback(new Error('实践学时不能为负数'));
                        } else {
                            callback();
                        }
                    }, trigger: ['blur', 'change'] }
                ],
                courseNature: [
                    { required: true, message: '请选择课程性质', trigger: 'change' }
                ]
            },
            
            // 培养方案模块
            trainingProgramFilters: {
                majorId: '',
                batchId: ''
            },
            trainingPrograms: [],
            addTrainingProgramDialogVisible: false,
            editTrainingProgramDialogVisible: false,
            importFromUrlDialogVisible: false,
            importFromUrlForm: {
                url: '',
                majorId: '',
                batchId: ''
            },
            importFromUrlRules: {
                url: [
                    { required: true, message: '请输入培养方案URL', trigger: 'blur' },
                    { type: 'url', message: '请输入有效的URL地址', trigger: 'blur' }
                ],
                majorId: [
                    { required: true, message: '请选择所属专业', trigger: 'change' }
                ],
                batchId: [
                    { required: true, message: '请选择届次', trigger: 'change' }
                ]
            },
            trainingProgramForm: {
                programId: null,
                majorId: '',
                batchId: '',
                programName: '',
                totalCredits: 150.0,
                totalCourses: 0,
                programStatus: 'enabled'
            },
            trainingProgramRules: {
                programName: [
                    { required: true, message: '请输入方案名称', trigger: 'blur' },
                    { min: 2, max: 200, message: '长度在 2 到 200 个字符', trigger: 'blur' }
                ],
                majorId: [
                    { required: true, message: '请选择所属专业', trigger: 'change' }
                ],
                batchId: [
                    { required: true, message: '请选择届次', trigger: 'change' }
                ],
                totalCredits: [
                    { required: true, message: '请输入总学分', trigger: 'blur' },
                    { type: 'number', message: '总学分必须为数字', trigger: 'blur' }
                ]
            },
            
            // 知识点管理模块
            knowledgePointFilters: {
                courseId: '',
                chapterId: ''
            },
            knowledgePointsList: [],
            
            // 题库管理模块
            questionBankFilters: {
                courseId: '',
                chapterId: '',
                questionType: ''
            },
            questionsList: [],
            addQuestionDialogVisible: false,
            editQuestionDialogVisible: false,
            questionForm: {
                questionId: null,
                questionContent: '',
                questionType: 'multipleChoice',
                chapterId: '',
                courseId: '',
                difficultyLevel: 'medium',
                optionA: '',
                optionB: '',
                optionC: '',
                optionD: '',
                optionE: '',
                correctAnswer: '',
                analysis: '',
                pointIds: []
            },
            questionRules: {
                questionContent: [
                    { required: true, message: '请输入题目内容', trigger: 'blur' },
                    { min: 10, max: 1000, message: '长度在 10 到 1000 个字符', trigger: 'blur' }
                ],
                chapterId: [
                    { required: true, message: '请选择所属章节', trigger: 'change' }
                ],
                difficultyLevel: [
                    { required: true, message: '请选择难度等级', trigger: 'change' }
                ],
                correctAnswer: [
                    { required: true, message: '请输入正确答案', trigger: 'blur' }
                ]
            },
            
            // 试题组卷模块
            testPaperForm: {
                paperName: '',
                courseId: '',
                chapterIds: [],
                questionCount: 10
            },
            generatedTestPaper: null,
            
            // 共享数据
            majors: [],
            batches: [],
            semesters: [],
            courses: [],
            teachers: [],
            chapters: [],
            // 测试规则
            testPaperRules: {
                paperName: [
                    { required: true, message: '请输入试卷名称', trigger: 'blur' },
                    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
                ],
                courseId: [
                    { required: true, message: '请选择课程', trigger: 'change' }
                ],
                questionCount: [
                    { required: true, message: '请输入题目数量', trigger: 'blur' },
                    { type: 'number', min: 5, max: 50, message: '题目数量在 5 到 50 之间', trigger: 'blur' }
                ]
            },
            
            // 教学计划管理
            teachingPlanFilters: {
                majorId: '',
                batchId: '',
                semesterId: '',
                courseId: ''
            },
            teachingPlans: [],
            teachingPlanStatistics: null,
            
            // 课程目录模块
            courseCatalogFilters: {
                majorId: '',
                courseId: ''
            },
            courseTreeData: [],
            chapters: [],
            addChapterDialogVisible: false,
            editChapterDialogVisible: false,
            chapterForm: {
                chapterId: null,
                courseId: null,
                chapterName: '',
                chapterDescription: '',
                chapterOrder: 1
            },
            treeProps: {
                label: 'name',
                children: 'children'
            },
            chapterRules: {
                chapterName: [
                    { required: true, message: '请输入章节名称', trigger: 'blur' },
                    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
                ],
                chapterDescription: [
                    { max: 500, message: '章节描述不能超过500个字符', trigger: 'blur' }
                ]
            },
            
            // 知识点管理模块
            selectedChapter: null,
            knowledgePoints: [],
            addKnowledgePointDialogVisible: false,
            editKnowledgePointDialogVisible: false,
            knowledgePointForm: {
                pointId: null,
                chapterId: null,
                pointName: '',
                pointDescription: '',
                keyPoints: '',
                level: 1,
                parentId: null
            },
            knowledgePointRules: {
                pointName: [
                    { required: true, message: '请输入知识点名称', trigger: 'blur' },
                    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
                ],
                pointDescription: [
                    { max: 1000, message: '知识点描述不能超过1000个字符', trigger: 'blur' }
                ],
                keyPoints: [
                    { max: 1000, message: '核心考点不能超过1000个字符', trigger: 'blur' }
                ]
            },
            
            // 章节小测模块
            chapterQuizFilters: {
                majorId: '',
                courseId: '',
                chapterId: '',
                questionCount: 10
            },
            quizQuestions: [],
            quizResult: null,
            
            // 成绩统计模块
            scoreStatisticsFilters: {
                majorId: '',
                courseId: '',
                examType: ''
            },
            scoreDistributionData: [],
            knowledgePointData: [],
            
            // API基础URL
            API_BASE_URL: '/ums/api',
            
            // 数据缓存配置
            cache: {},
            cacheExpiry: {},
            cacheDuration: 300000, // 5分钟缓存
            
            // 用户管理模块
            users: [],
            userFilters: {
                username: '',
                role: ''
            },
            addUserDialogVisible: false,
            editUserDialogVisible: false,
            userForm: {
                userId: null,
                username: '',
                password: '',
                name: '',
                email: '',
                phone: '',
                role: '',
                status: 'enabled'
            },
            userRules: {
                username: [
                    { required: true, message: '请输入用户名', trigger: 'blur' },
                    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
                ],
                password: [
                    { required: true, message: '请输入密码', trigger: 'blur' },
                    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
                ],
                name: [
                    { required: true, message: '请输入姓名', trigger: 'blur' },
                    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
                ],
                email: [
                    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
                ],
                phone: [
                    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
                ],
                role: [
                    { required: true, message: '请选择角色', trigger: 'change' }
                ],
                status: [
                    { required: true, message: '请选择状态', trigger: 'change' }
                ]
            }
        };
    },
    mounted() {
        // 检查是否已经登录
        this.checkLoginStatus();
    },
    methods: {
        // 登录相关方法
        checkLoginStatus() {
            // 先检查localStorage
            const userInfo = localStorage.getItem('userInfo');
            if (userInfo) {
                this.currentUser = JSON.parse(userInfo);
                this.isLoggedIn = true;
                this.loadSharedData(); // 立即加载共享数据，确保页面正常显示
                
                // 验证登录状态是否有效
                this.apiFetch(`${this.API_BASE_URL}/auth/current-user`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Not authenticated');
                    }
                    return response.json();
                })
                .then(data => {
                    if (data) {
                        // 更新用户信息
                        this.currentUser = data;
                        localStorage.setItem('userInfo', JSON.stringify(this.currentUser));
                    }
                    // 即使返回null，也保持已登录状态，不清除localStorage
                })
                .catch(error => {
                    // 网络错误或认证失败，只打印错误日志，保持当前登录状态
                    console.error('验证登录状态失败:', error);
                    // 不清除localStorage，保持当前登录状态，确保页面可以正常使用
                });
            } else {
                // 没有localStorage数据，显示登录页面
                this.isLoggedIn = false;
                this.currentUser = { username: '', role: '' };
            }
        },
        
        login() {
            this.$refs.loginForm.validate((valid) => {
                if (valid) {
                    // 登录请求
                    this.apiFetch(`${this.API_BASE_URL}/auth/login`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(this.loginForm)
                    })
                    .then(response => {
                        if (!response.ok) {
                            throw new Error('Network response was not ok');
                        }
                        return response.json().catch(() => ({})); // 处理非JSON响应
                    })
                    .then(data => {
                        if (data.success) {
                            this.currentUser = data.user;
                            this.isLoggedIn = true;
                            // 保存登录状态到localStorage
                            localStorage.setItem('userInfo', JSON.stringify(this.currentUser));
                            this.$message.success('登录成功');
                            this.loadSharedData();
                        } else {
                            this.$message.error(data.message || '登录失败');
                        }
                    })
                    .catch(error => {
                        this.$message.error('登录失败，请检查网络连接或用户名密码');
                    });
                }
            });
        },
        
        logout() {
            // 调用后端退出登录API
            fetch(`${this.API_BASE_URL}/auth/logout`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'include' // 确保携带认证cookie
            })
            .then(response => response.json())
            .then(data => {
                // 清除登录状态
                localStorage.removeItem('userInfo');
                this.isLoggedIn = false;
                this.currentUser = { username: '未登录', role: '' };
                this.$message.success('已退出登录');
                // 清除所有缓存
                this.clearCache();
            })
            .catch(error => {
                // 即使后端请求失败，也要清除本地登录状态
                localStorage.removeItem('userInfo');
                this.isLoggedIn = false;
                this.currentUser = { username: '未登录', role: '' };
                this.$message.success('已退出登录');
                // 清除所有缓存
                this.clearCache();
            });
        },
        
        // 处理下拉菜单命令
        handleDropdownCommand(command) {
            if (command === 'logout') {
                this.logout();
            }
        },
        
        // 缓存工具方法
        isCacheValid(key) {
            if (!this.cache[key]) return false;
            if (!this.cacheExpiry[key]) return false;
            return Date.now() < this.cacheExpiry[key];
        },
        
        getCachedData(key) {
            if (this.isCacheValid(key)) {
                return Promise.resolve(this.cache[key]);
            }
            return null;
        },
        
        setCachedData(key, data) {
            this.cache[key] = data;
            this.cacheExpiry[key] = Date.now() + this.cacheDuration;
        },
        
        clearCache(key) {
            if (key) {
                delete this.cache[key];
                delete this.cacheExpiry[key];
            } else {
                this.cache = {};
                this.cacheExpiry = {};
            }
        },
        
        // 通用fetch工具方法，自动添加credentials: 'include'选项
        apiFetch(url, options = {}) {
            const fetchOptions = {
                ...options,
                credentials: 'include'
            };
            return fetch(url, fetchOptions);
        },
        
        // 使用缓存或API获取数据
        fetchWithCache(key, url, options = {}) {
            // 先检查缓存
            const cachedData = this.getCachedData(key);
            if (cachedData) {
                return cachedData;
            }
            
            // 缓存无效，从API获取
            return this.apiFetch(url, options)
                .then(response => response.json())
                .then(data => {
                    // 缓存数据
                    this.setCachedData(key, data);
                    return data;
                })
                .catch(error => {
                    console.error(`获取${key}失败:`, error);
                    throw error;
                });
        },
        
        // 加载共享数据
        loadSharedData() {
            // 并行加载共享数据，提高加载效率
            const fetchData = (key, apiUrl, mockData) => {
                return this.fetchWithCache(key, apiUrl)
                    .then(data => {
                        if (data && data.success) {
                            this[key] = data.data;
                        } else {
                            this[key] = mockData;
                        }
                    })
                    .catch(error => {
                        console.error(`加载${key}失败:`, error);
                        this[key] = mockData;
                    });
            };

            Promise.all([
                // 加载专业数据
                fetchData('majors', `${this.API_BASE_URL}/majors`, [
                    { majorId: 1, majorName: '计算机科学与技术' },
                    { majorId: 2, majorName: '软件工程' },
                    { majorId: 3, majorName: '信息管理与信息系统' }
                ]),
                
                // 加载教师数据
                fetchData('teachers', `${this.API_BASE_URL}/teachers`, [
                    { teacherId: 1, teacherName: '张老师' },
                    { teacherId: 2, teacherName: '李老师' },
                    { teacherId: 3, teacherName: '王老师' }
                ]),
                
                // 加载届次数据
                fetchData('batches', `${this.API_BASE_URL}/batches`, [
                    { batchId: 1, batchYear: 2022 },
                    { batchId: 2, batchYear: 2023 },
                    { batchId: 3, batchYear: 2024 },
                    { batchId: 4, batchYear: 2025 }
                ]),
                
                // 加载学期数据
                fetchData('semesters', `${this.API_BASE_URL}/semesters`, [
                    { semesterId: 1, semesterName: '第一学期', academicYear: '2023-2024' },
                    { semesterId: 2, semesterName: '第二学期', academicYear: '2023-2024' }
                ])
            ]).then(() => {
                // 等基础数据加载完成后，再加载课程数据
                return fetchData('courses', `${this.API_BASE_URL}/courses`, [
                    { courseId: 1, courseName: '数据结构', majorId: 1 },
                    { courseId: 2, courseName: '算法设计与分析', majorId: 1 },
                    { courseId: 3, courseName: '数据库原理', majorId: 1 },
                    { courseId: 4, courseName: 'Java程序设计', majorId: 2 },
                    { courseId: 5, courseName: 'Web前端开发', majorId: 2 }
                ]);
            }).then(() => {
                // 根据当前活跃模块加载对应数据
                this.loadModuleData();
            });
        },
        
        // 根据当前活跃模块加载对应数据
        loadModuleData() {
            switch (this.activeModule) {
                case 'course-management':
                    this.queryCourses();
                    break;
                case 'training-program':
                    this.queryTrainingPrograms();
                    break;
                case 'knowledge-point':
                    this.queryKnowledgePoints();
                    break;
                case 'question-bank':
                    this.queryQuestions();
                    break;
                case 'user-management':
                    this.queryUsers();
                    break;
                // 可以根据需要添加更多模块
                default:
                    break;
            }
        },
        
        // 培养方案管理方法
        queryTrainingPrograms() {
            // 构建查询URL，包含筛选条件
            let url = `${this.API_BASE_URL}/training-programs/query?`;
            if (this.trainingProgramFilters.majorId) url += `majorId=${this.trainingProgramFilters.majorId}&`;
            if (this.trainingProgramFilters.batchId) url += `batchId=${this.trainingProgramFilters.batchId}&`;
            
            // 移除末尾的&或?
            url = url.replace(/[&?]$/, '');
            
            console.log('请求培养方案URL:', url);
            
            this.apiFetch(url)
            .then(response => {
                console.log('响应状态:', response.status);
                if (!response.ok) {
                    throw new Error(`HTTP错误! 状态: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                console.log('响应数据:', data);
                if (Array.isArray(data)) {
                    this.trainingPrograms = data;
                } else {
                    // 如果返回的是对象，检查是否有data字段
                    if (data && Array.isArray(data.data)) {
                        this.trainingPrograms = data.data;
                    } else {
                        console.log('响应数据不是数组，设置为空数组');
                        this.trainingPrograms = [];
                    }
                }
            })
            .catch(error => {
                console.error('获取培养方案列表失败:', error);
                this.$message.error('获取培养方案列表失败: ' + error.message);
            });
        },
        
        // 教学计划管理方法
        queryTeachingPlans() {
            // 构建查询URL，包含筛选条件
            let url = `${this.API_BASE_URL}/teaching-plans/query?`;
            if (this.teachingPlanFilters.majorId) url += `majorId=${this.teachingPlanFilters.majorId}&`;
            if (this.teachingPlanFilters.batchId) url += `batchId=${this.teachingPlanFilters.batchId}&`;
            if (this.teachingPlanFilters.semesterId) url += `semesterId=${this.teachingPlanFilters.semesterId}&`;
            if (this.teachingPlanFilters.courseId) url += `courseId=${this.teachingPlanFilters.courseId}&`;
            
            // 移除末尾的&或?
            url = url.replace(/[&?]$/, '');
            
            this.apiFetch(url)
            .then(response => response.json())
            .then(data => {
                if (Array.isArray(data)) {
                    this.teachingPlans = data;
                    // 获取统计信息
                    this.getTeachingPlanStatistics();
                } else {
                    // 模拟数据
                    this.teachingPlans = [
                        {
                            planId: 1,
                            major: { majorName: '计算机科学与技术' },
                            batch: { batchYear: 2023 },
                            semester: { semesterCode: '第一学期' },
                            course: { courseName: '数据结构', credits: 4.0, totalHours: 64, courseType: '必修' },
                            teachingGroup: '张老师',
                            planStatus: '生效',
                            version: 1
                        },
                        {
                            planId: 2,
                            major: { majorName: '计算机科学与技术' },
                            batch: { batchYear: 2023 },
                            semester: { semesterCode: '第一学期' },
                            course: { courseName: '算法设计与分析', credits: 3.0, totalHours: 48, courseType: '必修' },
                            teachingGroup: '李老师',
                            planStatus: '生效',
                            version: 1
                        }
                    ];
                    // 模拟统计信息
                    this.teachingPlanStatistics = {
                        totalCourses: 2,
                        totalCredits: 7.0,
                        totalHours: 112,
                        requiredCourseCount: 2,
                        electiveCourseCount: 0,
                        requiredCourseRatio: 100.0
                    };
                }
            })
            .catch(error => {
                // 模拟数据
                this.teachingPlans = [
                    {
                        planId: 1,
                        major: { majorName: '计算机科学与技术' },
                        batch: { batchYear: 2023 },
                        semester: { semesterCode: '第一学期' },
                        course: { courseName: '数据结构', credits: 4.0, totalHours: 64, courseType: '必修' },
                        teachingGroup: '张老师',
                        planStatus: '生效',
                        version: 1
                    },
                    {
                        planId: 2,
                        major: { majorName: '计算机科学与技术' },
                        batch: { batchYear: 2023 },
                        semester: { semesterCode: '第一学期' },
                        course: { courseName: '算法设计与分析', credits: 3.0, totalHours: 48, courseType: '必修' },
                        teachingGroup: '李老师',
                        planStatus: '生效',
                        version: 1
                    }
                ];
                // 模拟统计信息
                this.teachingPlanStatistics = {
                    totalCourses: 2,
                    totalCredits: 7.0,
                    totalHours: 112,
                    requiredCourseCount: 2,
                    electiveCourseCount: 0,
                    requiredCourseRatio: 100.0
                };
            });
        },
        
        // 获取教学计划统计信息
        getTeachingPlanStatistics() {
            // 构建查询URL，包含筛选条件
            let url = `${this.API_BASE_URL}/teaching-plans/statistics?`;
            if (this.teachingPlanFilters.majorId) url += `majorId=${this.teachingPlanFilters.majorId}&`;
            if (this.teachingPlanFilters.batchId) url += `batchId=${this.teachingPlanFilters.batchId}&`;
            if (this.teachingPlanFilters.semesterId) url += `semesterId=${this.teachingPlanFilters.semesterId}&`;
            
            // 移除末尾的&或?
            url = url.replace(/[&?]$/, '');
            
            fetch(url, {
                headers: {
                    'Accept': 'application/json'
                },
                credentials: 'include'
            })
            .then(response => response.json())
            .then(data => {
                if (data) {
                    this.teachingPlanStatistics = data;
                }
            })
            .catch(error => {
                console.error('获取统计信息失败:', error);
            });
        },
        
        // 归档教学计划
        archiveTeachingPlan(planId) {
            if (!planId) return;
            
            this.$confirm('确定要归档该教学计划吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                fetch(`${this.API_BASE_URL}/teaching-plans/${planId}/archive`, {
                    method: 'PUT'
                })
                .then(response => response.json())
                .then(data => {
                    if (data) {
                        this.$message.success('归档成功');
                        // 重新查询教学计划
                        this.queryTeachingPlans();
                    }
                })
                .catch(error => {
                    console.error('归档失败:', error);
                    this.$message.error('归档失败');
                });
            }).catch(() => {
                this.$message.info('已取消归档');
            });
        },
        
        // 作废教学计划
        invalidateTeachingPlan(planId) {
            if (!planId) return;
            
            this.$confirm('确定要作废该教学计划吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'danger'
            }).then(() => {
                fetch(`${this.API_BASE_URL}/teaching-plans/${planId}/invalidate`, {
                    method: 'PUT'
                })
                .then(response => response.json())
                .then(data => {
                    if (data) {
                        this.$message.success('作废成功');
                        // 重新查询教学计划
                        this.queryTeachingPlans();
                    }
                })
                .catch(error => {
                    console.error('作废失败:', error);
                    this.$message.error('作废失败');
                });
            }).catch(() => {
                this.$message.info('已取消作废');
            });
        },
        
        // 课程目录模块方法
        queryCourseCatalog() {
            // 查询章节列表
            fetch(`${this.API_BASE_URL}/chapters/course/${this.courseCatalogFilters.courseId}`)
                .then(response => response.json())
                .then(data => {
                    if (Array.isArray(data)) {
                        this.chapters = data;
                        // 更新章节表单的最大顺序值
                        if (data.length > 0) {
                            this.chapterForm.chapterOrder = data.length + 1;
                        } else {
                            this.chapterForm.chapterOrder = 1;
                        }
                    } else {
                        // 模拟数据
                        this.chapters = [
                            { chapterId: 1, chapterName: '第一章 绪论', courseId: 1, chapterOrder: 1, chapterDescription: '介绍数据结构的基本概念和算法分析方法' },
                            { chapterId: 2, chapterName: '第二章 线性表', courseId: 1, chapterOrder: 2, chapterDescription: '介绍线性表的定义、特点和存储结构' },
                            { chapterId: 3, chapterName: '第三章 栈和队列', courseId: 1, chapterOrder: 3, chapterDescription: '介绍栈和队列的基本概念和应用' },
                            { chapterId: 4, chapterName: '第四章 树和二叉树', courseId: 1, chapterOrder: 4, chapterDescription: '介绍树和二叉树的基本概念和遍历方法' }
                        ];
                    }
                    // 查询树形结构
                    this.queryCourseTree();
                })
                .catch(error => {
                    console.error('获取章节列表失败:', error);
                    // 模拟数据
                    this.chapters = [
                        { chapterId: 1, chapterName: '第一章 绪论', courseId: 1, chapterOrder: 1, chapterDescription: '介绍数据结构的基本概念和算法分析方法' },
                        { chapterId: 2, chapterName: '第二章 线性表', courseId: 1, chapterOrder: 2, chapterDescription: '介绍线性表的定义、特点和存储结构' },
                        { chapterId: 3, chapterName: '第三章 栈和队列', courseId: 1, chapterOrder: 3, chapterDescription: '介绍栈和队列的基本概念和应用' },
                        { chapterId: 4, chapterName: '第四章 树和二叉树', courseId: 1, chapterOrder: 4, chapterDescription: '介绍树和二叉树的基本概念和遍历方法' }
                    ];
                    // 查询树形结构
                    this.queryCourseTree();
                });
        },
        
        // 查询课程树形结构
        queryCourseTree() {
            if (!this.courseCatalogFilters.courseId) return;
            
            fetch(`${this.API_BASE_URL}/chapters/tree/course/${this.courseCatalogFilters.courseId}`, {
                credentials: 'include'
            })
            .then(response => response.json())
            .then(data => {
                    if (Array.isArray(data)) {
                        this.courseTreeData = data;
                    } else {
                        // 模拟数据
                        this.courseTreeData = [
                            {
                                name: '数据结构',
                                children: [
                                    {
                                        name: '第一章 绪论',
                                        children: [
                                            { name: '1.1 数据结构的基本概念' },
                                            { name: '1.2 算法及其分析' }
                                        ]
                                    },
                                    {
                                        name: '第二章 线性表',
                                        children: [
                                            { name: '2.1 线性表的定义和特点' },
                                            { name: '2.2 线性表的顺序存储结构' },
                                            { name: '2.3 线性表的链式存储结构' }
                                        ]
                                    }
                                ]
                            }
                        ];
                    }
                })
                .catch(error => {
                    console.error('获取课程树形结构失败:', error);
                    // 模拟数据
                    this.courseTreeData = [
                        {
                            name: '数据结构',
                            children: [
                                {
                                    name: '第一章 绪论',
                                    children: [
                                        { name: '1.1 数据结构的基本概念' },
                                        { name: '1.2 算法及其分析' }
                                    ]
                                },
                                {
                                    name: '第二章 线性表',
                                    children: [
                                        { name: '2.1 线性表的定义和特点' },
                                        { name: '2.2 线性表的顺序存储结构' },
                                        { name: '2.3 线性表的链式存储结构' }
                                    ]
                                }
                            ]
                        }
                    ];
                });
        },
        
        // 选择章节
        selectChapter(chapter) {
            this.selectedChapter = chapter;
            this.getKnowledgePointsByChapterId(chapter.chapterId);
        },
        
        // 获取章节的知识点
        getKnowledgePointsByChapterId(chapterId) {
            fetch(`${this.API_BASE_URL}/knowledge-points/chapter/${chapterId}`, {
                credentials: 'include'
            })
            .then(response => response.json())
            .then(data => {
                if (Array.isArray(data)) {
                    this.knowledgePoints = data;
                    this.knowledgePointsList = data;
                } else {
                    // 模拟数据
                    this.knowledgePoints = [
                        { pointId: 1, pointName: '知识点1', pointDescription: '知识点1的详细描述', keyPoints: '核心考点1', chapterId: chapterId },
                        { pointId: 2, pointName: '知识点2', pointDescription: '知识点2的详细描述', keyPoints: '核心考点2', chapterId: chapterId }
                    ];
                    this.knowledgePointsList = [];
                }
            })
            .catch(error => {
                console.error('获取知识点失败:', error);
                // 模拟数据
                this.knowledgePoints = [
                    { pointId: 1, pointName: '知识点1', pointDescription: '知识点1的详细描述', keyPoints: '核心考点1', chapterId: chapterId },
                    { pointId: 2, pointName: '知识点2', pointDescription: '知识点2的详细描述', keyPoints: '核心考点2', chapterId: chapterId }
                ];
                this.knowledgePointsList = [];
            });
        },
        
        // 查询知识点
        queryKnowledgePoints() {
            // 构建查询URL，包含筛选条件
            let url = `${this.API_BASE_URL}/knowledge-points?`;
            if (this.knowledgePointFilters.courseId) url += `courseId=${this.knowledgePointFilters.courseId}&`;
            if (this.knowledgePointFilters.chapterId) url += `chapterId=${this.knowledgePointFilters.chapterId}&`;
            
            // 移除末尾的&或?
            url = url.replace(/[&?]$/, '');
            
            console.log('查询知识点URL:', url);
            
            this.apiFetch(url)
            .then(response => {
                console.log('查询知识点响应状态:', response.status);
                return response.text();
            })
            .then(text => {
                console.log('查询知识点响应文本:', text);
                let data = null;
                if (text) {
                    try {
                        data = JSON.parse(text);
                    } catch (e) {
                        console.error('解析JSON失败:', e);
                    }
                }
                if (Array.isArray(data)) {
                    this.knowledgePoints = data;
                    this.knowledgePointsList = data;
                } else {
                    console.error('知识点响应数据不是数组:', data);
                    this.knowledgePoints = [];
                    this.knowledgePointsList = [];
                }
            })
            .catch(error => {
                console.error('获取知识点失败:', error);
                this.knowledgePoints = [];
                this.knowledgePointsList = [];
            });
        },
        
        // 处理课程选择变化
        handleCourseChange(courseId) {
            // 重置章节选择
            this.knowledgePointFilters.chapterId = '';
            // 根据选择的课程ID加载章节列表
            if (courseId) {
                fetch(`${this.API_BASE_URL}/chapters/course/${courseId}`, {
                credentials: 'include'
            })
            .then(response => response.json())
            .then(data => {
                    if (Array.isArray(data)) {
                        this.chapters = data;
                    } else {
                        this.chapters = [];
                    }
                    // 加载完章节后自动查询知识点
                    this.queryKnowledgePoints();
                })
                .catch(error => {
                    console.error('获取章节列表失败:', error);
                    this.chapters = [];
                    // 即使获取章节失败，也尝试查询知识点
                    this.queryKnowledgePoints();
                });
            } else {
                this.chapters = [];
                // 清空课程选择后，查询所有知识点或清空列表
                this.queryKnowledgePoints();
            }
        },
        
        // 处理知识点级别变化
        handleLevelChange() {
            // 如果级别改为1，重置父知识点
            if (this.knowledgePointForm.level === 1) {
                this.knowledgePointForm.parentId = null;
            }
        },
        
        // 显示添加知识点对话框
        showAddKnowledgePointDialog() {
            // 检查是否选择了章节
            if (!this.knowledgePointFilters.chapterId) {
                this.$message.warning('请先选择课程和章节');
                return;
            }
            
            this.knowledgePointForm = {
                pointId: null,
                chapterId: this.knowledgePointFilters.chapterId,
                pointName: '',
                pointDescription: '',
                keyPoints: '',
                level: 1,
                parentId: null
            };
            this.addKnowledgePointDialogVisible = true;
        },
        
        // 显示编辑知识点对话框
        showEditKnowledgePointDialog(knowledgePoint) {
            this.knowledgePointForm = JSON.parse(JSON.stringify(knowledgePoint));
            this.editKnowledgePointDialogVisible = true;
        },
        
        // 保存知识点
        saveKnowledgePoint() {
            this.$refs.knowledgePointForm.validate((valid) => {
                if (valid) {
                    const isEdit = this.knowledgePointForm.pointId !== null;
                    const url = isEdit 
                        ? `${this.API_BASE_URL}/knowledge-points/${this.knowledgePointForm.pointId}` 
                        : `${this.API_BASE_URL}/knowledge-points`;
                    const method = isEdit ? 'PUT' : 'POST';
                    
                    // 构建知识点数据，确保包含所有必要字段
                    const knowledgePointData = {
                        pointId: this.knowledgePointForm.pointId,
                        chapterId: this.knowledgePointForm.chapterId,
                        pointName: this.knowledgePointForm.pointName,
                        pointDescription: this.knowledgePointForm.pointDescription,
                        keyPoints: this.knowledgePointForm.keyPoints,
                        level: this.knowledgePointForm.level,
                        parentId: this.knowledgePointForm.parentId
                    };
                    
                    this.apiFetch(url, {
                        method: method,
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(knowledgePointData)
                    })
                    .then(response => {
                        console.log('知识点保存响应状态:', response.status);
                        console.log('知识点保存响应头:', response.headers);
                        return response.text();
                    })
                    .then(text => {
                        console.log('知识点保存响应文本:', text);
                        let data = null;
                        if (text) {
                            try {
                                data = JSON.parse(text);
                            } catch (e) {
                                console.error('解析JSON失败:', e);
                            }
                        }
                        this.$message.success(isEdit ? '知识点更新成功' : '知识点添加成功');
                        if (isEdit) {
                            this.editKnowledgePointDialogVisible = false;
                        } else {
                            this.addKnowledgePointDialogVisible = false;
                        }
                        // 直接刷新知识点列表，使用当前筛选条件
                        if (this.activeModule === 'knowledge-point') {
                            this.queryKnowledgePoints();
                        } else if (this.selectedChapter) {
                            this.getKnowledgePointsByChapterId(this.selectedChapter.chapterId);
                        }
                    })
                    .catch(error => {
                        console.error('保存知识点失败:', error);
                        this.$message.error(isEdit ? '知识点更新失败' : '知识点添加失败: ' + error.message);
                    });
                }
            });
        },
        
        // 删除知识点
        deleteKnowledgePoint(pointId) {
            this.$confirm('确定要删除这个知识点吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.apiFetch(`${this.API_BASE_URL}/knowledge-points/${pointId}`, {
                    method: 'DELETE'
                })
                .then(response => {
                    console.log('知识点删除响应状态:', response.status);
                    if (response.ok) {
                        this.$message.success('知识点删除成功');
                        if (this.activeModule === 'knowledge-point') {
                            this.queryKnowledgePoints();
                        } else if (this.selectedChapter) {
                            this.getKnowledgePointsByChapterId(this.selectedChapter.chapterId);
                        }
                    } else {
                        throw new Error('删除知识点失败');
                    }
                })
                .catch(error => {
                    console.error('删除知识点失败:', error);
                    this.$message.error('知识点删除失败: ' + error.message);
                });
            }).catch(() => {
                this.$message.info('已取消删除');
            });
        },
        
        // 显示添加章节对话框
        showAddChapterDialog() {
            this.resetChapterForm();
            this.chapterForm.courseId = this.courseCatalogFilters.courseId;
            this.addChapterDialogVisible = true;
        },
        
        // 显示编辑章节对话框
        showEditChapterDialog(row) {
            this.resetChapterForm();
            // 复制章节数据到表单
            this.chapterForm = Object.assign({}, row);
            this.editChapterDialogVisible = true;
        },
        
        // 重置章节表单
        resetChapterForm() {
            this.chapterForm = {
                chapterId: null,
                courseId: this.courseCatalogFilters.courseId,
                chapterName: '',
                chapterDescription: '',
                chapterOrder: this.chapters.length + 1
            };
            // 重置表单验证
            if (this.$refs.chapterForm) {
                this.$refs.chapterForm.resetFields();
            }
        },
        
        // 生成试卷
        generateTestPaper() {
            this.$refs.testPaperForm.validate((valid) => {
                if (valid) {
                    // 调用API生成试卷
                    fetch(`${this.API_BASE_URL}/exam-papers/generate`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(this.testPaperForm)
                    })
                    .then(response => response.json())
                    .then(data => {
                        if (data && data.success) {
                            this.generatedTestPaper = data.data;
                            this.$message.success('试卷生成成功');
                        } else {
                            this.$message.error('试卷生成失败');
                        }
                    })
                    .catch(error => {
                        console.error('生成试卷失败:', error);
                        this.$message.error('生成试卷失败');
                    });
                }
            });
        },
        
        // 重置试卷表单
        resetTestPaperForm() {
            this.testPaperForm = {
                paperName: '',
                courseId: '',
                chapterIds: [],
                questionCount: 10
            };
            // 重置表单验证
            if (this.$refs.testPaperForm) {
                this.$refs.testPaperForm.resetFields();
            }
        },
        
        // 下载试卷
        downloadTestPaper() {
            // 实现下载试卷功能
            this.$message.success('下载功能待实现');
        },
        
        // 保存章节
        saveChapter() {
            this.$refs.chapterForm.validate((valid) => {
                if (valid) {
                    // 构建请求URL和方法
                    let url = `${this.API_BASE_URL}/chapters`;
                    let method = 'POST';
                    if (this.chapterForm.chapterId) {
                        url += `/${this.chapterForm.chapterId}`;
                        method = 'PUT';
                    }
                    
                    // 发送请求
                    fetch(url, {
                        method: method,
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(this.chapterForm)
                    })
                    .then(response => response.json())
                    .then(data => {
                        this.$message.success(this.chapterForm.chapterId ? '章节编辑成功' : '章节添加成功');
                        // 关闭对话框
                        this.addChapterDialogVisible = false;
                        this.editChapterDialogVisible = false;
                        // 重新查询章节列表
                        this.queryCourseCatalog();
                    })
                    .catch(error => {
                        console.error('保存章节失败:', error);
                        this.$message.error(this.chapterForm.chapterId ? '章节编辑失败' : '章节添加失败');
                    });
                }
            });
        },
        
        // 删除章节
        deleteChapter(chapterId) {
            this.$confirm('确定要删除该章节吗？删除后相关知识点和题目数据也会被同步删除。', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'danger'
            }).then(() => {
                fetch(`${this.API_BASE_URL}/chapters/${chapterId}`, {
                    method: 'DELETE'
                })
                .then(response => {
                    if (response.ok) {
                        this.$message.success('章节删除成功');
                        // 重新查询章节列表
                        this.queryCourseCatalog();
                    } else {
                        throw new Error('删除失败');
                    }
                })
                .catch(error => {
                    console.error('删除章节失败:', error);
                    this.$message.error('章节删除失败');
                });
            }).catch(() => {
                this.$message.info('已取消删除');
            });
        },
        
        // 移动章节
        moveChapter(row, direction) {
            let oldOrder = row.chapterOrder;
            let newOrder = oldOrder;
            let targetRow = null;
            
            // 查找目标行
            if (direction === 'up' && oldOrder > 1) {
                newOrder = oldOrder - 1;
                targetRow = this.chapters.find(chapter => chapter.chapterOrder === newOrder);
            } else if (direction === 'down' && oldOrder < this.chapters.length) {
                newOrder = oldOrder + 1;
                targetRow = this.chapters.find(chapter => chapter.chapterOrder === newOrder);
            }
            
            if (targetRow) {
                // 先更新前端显示
                row.chapterOrder = newOrder;
                targetRow.chapterOrder = oldOrder;
                // 按顺序重新排序数组
                this.chapters.sort((a, b) => a.chapterOrder - b.chapterOrder);
                
                // 向服务器发送请求更新顺序
                fetch(`${this.API_BASE_URL}/chapters/adjust-order`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify([
                        { chapterId: row.chapterId, chapterOrder: newOrder },
                        { chapterId: targetRow.chapterId, chapterOrder: oldOrder }
                    ])
                })
                .then(response => {
                    if (response.ok) {
                        this.$message.success('章节顺序调整成功');
                    } else {
                        throw new Error('调整失败');
                    }
                })
                .catch(error => {
                    console.error('调整章节顺序失败:', error);
                    this.$message.error('章节顺序调整失败');
                    // 恢复原始顺序
                    row.chapterOrder = oldOrder;
                    targetRow.chapterOrder = newOrder;
                    this.chapters.sort((a, b) => a.chapterOrder - b.chapterOrder);
                });
            }
        },
        
        // 章节小测模块方法
        generateChapterQuiz() {
            fetch(`${this.API_BASE_URL}/chapter-quiz/generate`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(this.chapterQuizFilters)
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    this.quizQuestions = data.data;
                    this.quizResult = null;
                } else {
                    // 模拟数据
                    this.quizQuestions = [
                        {
                            questionId: 1,
                            questionContent: '数据结构中，线性结构包括下列哪项？',
                            optionA: '数组',
                            optionB: '链表',
                            optionC: '栈',
                            optionD: '以上都是',
                            correctAnswer: 'D',
                            userAnswer: ''
                        },
                        {
                            questionId: 2,
                            questionContent: '栈的特点是什么？',
                            optionA: '先进先出',
                            optionB: '后进先出',
                            optionC: '随机存取',
                            optionD: '顺序存取',
                            correctAnswer: 'B',
                            userAnswer: ''
                        },
                        {
                            questionId: 3,
                            questionContent: '队列的特点是什么？',
                            optionA: '先进先出',
                            optionB: '后进先出',
                            optionC: '随机存取',
                            optionD: '顺序存取',
                            correctAnswer: 'A',
                            userAnswer: ''
                        }
                    ];
                    this.quizResult = null;
                }
            })
            .catch(error => {
                // 模拟数据
                this.quizQuestions = [
                    {
                        questionId: 1,
                        questionContent: '数据结构中，线性结构包括下列哪项？',
                        optionA: '数组',
                        optionB: '链表',
                        optionC: '栈',
                        optionD: '以上都是',
                        correctAnswer: 'D',
                        userAnswer: ''
                    },
                    {
                        questionId: 2,
                        questionContent: '栈的特点是什么？',
                        optionA: '先进先出',
                        optionB: '后进先出',
                        optionC: '随机存取',
                        optionD: '顺序存取',
                        correctAnswer: 'B',
                        userAnswer: ''
                    },
                    {
                        questionId: 3,
                        questionContent: '队列的特点是什么？',
                        optionA: '先进先出',
                        optionB: '后进先出',
                        optionC: '随机存取',
                        optionD: '顺序存取',
                        correctAnswer: 'A',
                        userAnswer: ''
                    }
                ];
                this.quizResult = null;
            });
        },
        
        submitQuiz() {
            let correctCount = 0;
            let totalScore = 0;
            
            this.quizQuestions.forEach(question => {
                if (question.userAnswer === question.correctAnswer) {
                    correctCount++;
                    totalScore += 10;
                }
            });
            
            this.quizResult = {
                totalScore: totalScore,
                correctCount: correctCount,
                totalCount: this.quizQuestions.length
            };
            
            // 保存成绩到服务器
            fetch(`${this.API_BASE_URL}/chapter-quiz/submit`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    questions: this.quizQuestions,
                    result: this.quizResult
                })
            });
        },
        
        resetQuiz() {
            this.quizQuestions = [];
            this.quizResult = null;
        },
        
        // 成绩统计模块方法
        queryScoreStatistics() {
            fetch(`${this.API_BASE_URL}/score-statistics/query`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(this.scoreStatisticsFilters)
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // 处理成绩分布数据
                    const distribution = data.data.scoreDistribution;
                    const total = distribution.reduce((sum, count) => sum + count, 0);
                    this.scoreDistributionData = [
                        { range: '60分以下', count: distribution[0], percentage: ((distribution[0] / total) * 100).toFixed(1) + '%' },
                        { range: '60-70分', count: distribution[1], percentage: ((distribution[1] / total) * 100).toFixed(1) + '%' },
                        { range: '70-80分', count: distribution[2], percentage: ((distribution[2] / total) * 100).toFixed(1) + '%' },
                        { range: '80-90分', count: distribution[3], percentage: ((distribution[3] / total) * 100).toFixed(1) + '%' },
                        { range: '90-100分', count: distribution[4], percentage: ((distribution[4] / total) * 100).toFixed(1) + '%' },
                        { range: '100分以上', count: distribution[5], percentage: ((distribution[5] / total) * 100).toFixed(1) + '%' }
                    ];
                    
                    // 处理知识点掌握情况数据
                    this.knowledgePointData = data.data.knowledgePointScores.map(item => {
                        let level = '及格';
                        if (item.score >= 90) {
                            level = '优秀';
                        } else if (item.score >= 80) {
                            level = '良好';
                        } else if (item.score >= 60) {
                            level = '及格';
                        } else {
                            level = '不及格';
                        }
                        return {
                            name: item.name,
                            score: item.score,
                            level: level
                        };
                    });
                } else {
                    // 模拟数据
                    this.scoreDistributionData = [
                        { range: '60分以下', count: 60, percentage: '15.0%' },
                        { range: '60-70分', count: 80, percentage: '20.0%' },
                        { range: '70-80分', count: 120, percentage: '30.0%' },
                        { range: '80-90分', count: 100, percentage: '25.0%' },
                        { range: '90-100分', count: 30, percentage: '7.5%' },
                        { range: '100分以上', count: 10, percentage: '2.5%' }
                    ];
                    
                    this.knowledgePointData = [
                        { name: '大数据概述', score: 85, level: '良好' },
                        { name: 'Hadoop生态系统', score: 78, level: '良好' },
                        { name: 'MapReduce编程', score: 92, level: '优秀' },
                        { name: 'HDFS分布式文件系统', score: 88, level: '良好' },
                        { name: 'YARN资源管理', score: 75, level: '良好' },
                        { name: 'Spark核心编程', score: 90, level: '优秀' },
                        { name: 'Spark SQL', score: 82, level: '良好' },
                        { name: '数据仓库设计', score: 79, level: '良好' },
                        { name: '数据可视化', score: 86, level: '良好' },
                        { name: '大数据项目实战', score: 81, level: '良好' }
                    ];
                }
            })
            .catch(error => {
                console.error('获取成绩统计失败:', error);
                // 模拟数据
                this.scoreDistributionData = [
                    { range: '60分以下', count: 60, percentage: '15.0%' },
                    { range: '60-70分', count: 80, percentage: '20.0%' },
                    { range: '70-80分', count: 120, percentage: '30.0%' },
                    { range: '80-90分', count: 100, percentage: '25.0%' },
                    { range: '90-100分', count: 30, percentage: '7.5%' },
                    { range: '100分以上', count: 10, percentage: '2.5%' }
                ];
                
                this.knowledgePointData = [
                    { name: '大数据概述', score: 85, level: '良好' },
                    { name: 'Hadoop生态系统', score: 78, level: '良好' },
                    { name: 'MapReduce编程', score: 92, level: '优秀' },
                    { name: 'HDFS分布式文件系统', score: 88, level: '良好' },
                    { name: 'YARN资源管理', score: 75, level: '良好' },
                    { name: 'Spark核心编程', score: 90, level: '优秀' },
                    { name: 'Spark SQL', score: 82, level: '良好' },
                    { name: '数据仓库设计', score: 79, level: '良好' },
                    { name: '数据可视化', score: 86, level: '良好' },
                    { name: '大数据项目实战', score: 81, level: '良好' }
                ];
            });
        },
        
        // 数据导入导出模块方法
        handleQuestionImportSuccess(response) {
            this.$message.success(`试题导入成功，共导入 ${response.data} 条数据`);
        },
        
        handleStudentImportSuccess(response) {
            this.$message.success(`学生信息导入成功，共导入 ${response.data} 条数据`);
        },
        
        handleScoreImportSuccess(response) {
            this.$message.success(`成绩导入成功，共导入 ${response.data} 条数据`);
        },
        
        handleImportError(err) {
            let errorMsg = '导入失败，请检查文件格式';
            if (err && err.response && err.response.data) {
                errorMsg = err.response.data;
            }
            this.$message.error(errorMsg);
        },
        
        submitQuestionUpload() {
            this.$refs.questionUpload.submit();
        },
        
        submitStudentUpload() {
            this.$refs.studentUpload.submit();
        },
        
        submitScoreUpload() {
            this.$refs.scoreUpload.submit();
        },

        submitDatabaseCourseUpload() {
            this.$refs.databaseCourseUpload.submit();
        },

        handleDatabaseCourseImportSuccess(response) {
            this.$message.success(`数据库课程和知识点导入成功，共导入 ${response.courses.length} 门课程和 ${response.knowledgePoints.length} 个知识点`);
        },

        exportTeachingPlans(format) {
            // 处理事件对象，确保format是有效字符串
            if (typeof format === 'object' && format.type) {
                format = 'excel'; // 默认为excel格式
            }
            // 构建导出URL，包含筛选条件
            let url = `${this.API_BASE_URL}/teaching-plans/export/${format}?`;
            if (this.teachingPlanFilters.majorId) url += `majorId=${this.teachingPlanFilters.majorId}&`;
            if (this.teachingPlanFilters.batchId) url += `batchId=${this.teachingPlanFilters.batchId}&`;
            if (this.teachingPlanFilters.semesterId) url += `semesterId=${this.teachingPlanFilters.semesterId}&`;
            if (this.teachingPlanFilters.courseId) url += `courseId=${this.teachingPlanFilters.courseId}&`;
            
            // 移除末尾的&或?
            url = url.replace(/[&?]$/, '');
            window.location.href = url;
        },
        
        exportCourseCatalog() {
            window.location.href = `${this.API_BASE_URL}/export/course-catalog`;
        },
        
        exportScoreStatistics() {
            window.location.href = `${this.API_BASE_URL}/export/score-statistics`;
        },
        
        // 用户管理模块方法
        queryUsers() {
            // 构建查询URL，包含筛选条件
            let url = `${this.API_BASE_URL}/users?`;
            if (this.userFilters.username) url += `username=${this.userFilters.username}&`;
            if (this.userFilters.role) url += `role=${this.userFilters.role}&`;
            
            // 移除末尾的&或?
            url = url.replace(/[&?]$/, '');
            
            fetch(url, {
                credentials: 'include'
            })
            .then(response => response.json())
            .then(data => {
                if (data && data.success) {
                    // 处理 { success: true, data: [...] } 格式
                    this.users = data.data;
                } else if (Array.isArray(data)) {
                    // 兼容直接返回数组的格式
                    this.users = data;
                } else {
                    this.$message.error('获取用户列表失败');
                }
            })
            .catch(error => {
                console.error('获取用户列表失败:', error);
                this.$message.error('获取用户列表失败');
            });
        },
        
        showAddUserDialog() {
            this.userForm = {
                userId: null,
                username: '',
                password: '',
                name: '',
                email: '',
                phone: '',
                role: '',
                status: 'enabled'
            };
            this.addUserDialogVisible = true;
        },
        
        showEditUserDialog(user) {
            this.userForm = {
                userId: user.userId,
                username: user.username,
                password: '', // 编辑时不显示密码
                name: user.name,
                email: user.email,
                phone: user.phone,
                role: user.role,
                status: user.status
            };
            this.editUserDialogVisible = true;
        },
        
        saveUser() {
            this.$refs.userForm.validate((valid) => {
                if (valid) {
                    // 新增用户
                    fetch(`${this.API_BASE_URL}/users`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(this.userForm)
                    })
                    .then(response => response.json())
                    .then(data => {
                        if (data) {
                            this.$message.success('新增用户成功');
                            this.addUserDialogVisible = false;
                            this.queryUsers();
                        } else {
                            this.$message.error('新增用户失败');
                        }
                    })
                    .catch(error => {
                        console.error('新增用户失败:', error);
                        this.$message.error('新增用户失败');
                    });
                }
            });
        },
        
        updateUser() {
            this.$refs.userForm.validate((valid) => {
                if (valid) {
                    // 创建一个新对象，不包含空密码
                    const updateData = { ...this.userForm };
                    if (!updateData.password) {
                        delete updateData.password;
                    }
                    
                    // 更新用户
                    fetch(`${this.API_BASE_URL}/users/${updateData.userId}`, {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(updateData)
                    })
                    .then(response => response.json())
                    .then(data => {
                        if (data) {
                            this.$message.success('更新用户成功');
                            this.editUserDialogVisible = false;
                            this.queryUsers();
                        } else {
                            this.$message.error('更新用户失败');
                        }
                    })
                    .catch(error => {
                        console.error('更新用户失败:', error);
                        this.$message.error('更新用户失败');
                    });
                }
            });
        },
        
        deleteUser(userId) {
            this.$confirm('确定要删除该用户吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'danger'
            }).then(() => {
                fetch(`${this.API_BASE_URL}/users/${userId}`, {
                    method: 'DELETE'
                })
                .then(response => response.json())
                .then(data => {
                    if (data) {
                        this.$message.success('删除用户成功');
                        this.queryUsers();
                    } else {
                        this.$message.error('删除用户失败');
                    }
                })
                .catch(error => {
                    console.error('删除用户失败:', error);
                    this.$message.error('删除用户失败');
                });
            }).catch(() => {
                this.$message.info('已取消删除');
            });
        },
        
        // 课程管理模块方法
        queryCourses() {
            // 构建查询URL，包含筛选条件
            let url = `${this.API_BASE_URL}/courses`;
            
            fetch(url, {
                headers: {
                    'Accept': 'application/json'
                },
                credentials: 'include'
            })
            .then(response => {
                console.log('获取课程列表响应:', response.status, response.statusText);
                return response.json();
            })
            .then(data => {
                if (data && data.success) {
                    let courses = data.data;
                    
                    // 根据筛选条件过滤课程
                    if (this.courseManagementFilters.majorId) {
                        courses = courses.filter(course => 
                            // 处理新课程，有major属性的情况
                            (course.major && course.major.majorId == this.courseManagementFilters.majorId) || 
                            // 处理旧课程，只有majorId字段的情况
                            (course.majorId && course.majorId == this.courseManagementFilters.majorId)
                        );
                    }
                    
                    if (this.courseManagementFilters.courseName) {
                        courses = courses.filter(course => course.courseName.includes(this.courseManagementFilters.courseName));
                    }
                    
                    this.coursesList = courses;
                } else {
                    this.$message.error('获取课程列表失败');
                }
            })
            .catch(error => {
                console.error('获取课程列表失败:', error);
                this.$message.error('获取课程列表失败');
            });
        },
        
        // 处理查询按钮点击事件
        handleQuery() {
            this.queryCourses();
        },
        
        // 处理重置按钮点击事件
        handleReset() {
            // 清空筛选条件
            this.courseManagementFilters = {
                majorId: '',
                courseName: ''
            };
            // 重新查询课程列表
            this.queryCourses();
        },
        
        showAddCourseDialog() {
            this.courseForm = {
                courseId: null,
                courseName: '',
                majorId: '',
                credits: 3.0,
                totalHours: 48,
                courseType: '',
                courseCode: '',
                theoryHours: 0,
                practiceHours: 0,
                courseNature: '',
                courseStatus: 'enabled'
            };
            this.addCourseDialogVisible = true;
        },
        
        showEditCourseDialog(row) {
            this.courseForm = JSON.parse(JSON.stringify(row));
            // 确保课程状态有有效值
            if (!this.courseForm.courseStatus || this.courseForm.courseStatus === '') {
                this.courseForm.courseStatus = 'enabled';
            }
            // 确保理论学时和实践学时有有效值
            if (this.courseForm.theoryHours === null || this.courseForm.theoryHours === undefined || isNaN(this.courseForm.theoryHours)) {
                this.courseForm.theoryHours = 0;
            }
            if (this.courseForm.practiceHours === null || this.courseForm.practiceHours === undefined || isNaN(this.courseForm.practiceHours)) {
                this.courseForm.practiceHours = 0;
            }
            // 确保总学时有有效值
            if (this.courseForm.totalHours === null || this.courseForm.totalHours === undefined || isNaN(this.courseForm.totalHours)) {
                this.courseForm.totalHours = 0;
            }
            this.editCourseDialogVisible = true;
        },
        
        // 培养方案模块方法
        showAddTrainingProgramDialog() {
            this.trainingProgramForm = {
                programId: null,
                majorId: '',
                batchId: '',
                programName: '',
                totalCredits: 150.0,
                totalCourses: 0,
                programStatus: 'enabled'
            };
            this.addTrainingProgramDialogVisible = true;
        },
        
        showEditTrainingProgramDialog(row) {
            this.trainingProgramForm = JSON.parse(JSON.stringify(row));
            this.editTrainingProgramDialogVisible = true;
        },
        
        // 题库管理模块方法
        showAddQuestionDialog() {
            this.questionForm = {
                questionId: null,
                questionContent: '',
                questionType: 'multipleChoice',
                chapterId: '',
                courseId: '',
                difficultyLevel: 'medium',
                optionA: '',
                optionB: '',
                optionC: '',
                optionD: '',
                optionE: '',
                correctAnswer: '',
                analysis: '',
                pointIds: []
            };
            this.addQuestionDialogVisible = true;
        },
        
        showEditQuestionDialog(row) {
            this.questionForm = JSON.parse(JSON.stringify(row));
            this.editQuestionDialogVisible = true;
        },
        
        // 保存题目
        saveQuestion() {
            this.$refs.questionForm.validate((valid) => {
                if (valid) {
                    const isEdit = this.questionForm.questionId !== null;
                    const url = isEdit 
                        ? `${this.API_BASE_URL}/questions/${this.questionForm.questionId}` 
                        : `${this.API_BASE_URL}/questions`;
                    const method = isEdit ? 'PUT' : 'POST';
                    
                    fetch(url, {
                        method: method,
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(this.questionForm)
                    })
                    .then(response => response.json())
                    .then(data => {
                        this.$message.success(isEdit ? '题目更新成功' : '题目添加成功');
                        if (isEdit) {
                            this.editQuestionDialogVisible = false;
                        } else {
                            this.addQuestionDialogVisible = false;
                        }
                        this.queryQuestions();
                    })
                    .catch(error => {
                        console.error('保存题目失败:', error);
                        this.$message.error(isEdit ? '题目更新失败' : '题目添加失败');
                    });
                }
            });
        },
        
        // 保存课程
        saveCourse() {
            console.log('开始保存课程，表单数据:', this.courseForm);
            // 根据当前对话框状态确定使用哪个表单ref
            const formRef = this.editCourseDialogVisible ? 'editCourseForm' : 'courseForm';
            this.$refs[formRef].validate((valid) => {
                if (valid) {
                    console.log('表单验证通过，准备构建请求数据');
                    // 手动构建课程数据，只包含后端需要的字段
                    console.log('构建课程数据前，courseForm.majorId:', this.courseForm.majorId, '类型:', typeof this.courseForm.majorId);
                    
                    // 更严格的majorId处理：确保只有有效数值才会被发送，否则发送null
                    let majorIdValue = null;
                    // 检查majorId是否存在且是有效数值
                    const rawMajorId = this.courseForm.majorId;
                    if (rawMajorId !== undefined && rawMajorId !== null && rawMajorId !== '') {
                        // 尝试转换为整数
                        const parsed = parseInt(rawMajorId, 10);
                        // 只有当转换结果是有效整数时才使用，否则使用null
                        if (!isNaN(parsed) && isFinite(parsed) && Number.isInteger(parsed)) {
                            majorIdValue = parsed;
                        }
                    }
                    // 确保majorIdValue要么是null，要么是有效的整数
                    console.log('最终majorIdValue:', majorIdValue, '类型:', typeof majorIdValue);
                    
                    console.log('处理后的majorIdValue:', majorIdValue, '类型:', typeof majorIdValue);
                    
                    const courseData = {
                        courseCode: this.courseForm.courseCode,
                        courseName: this.courseForm.courseName,
                        majorId: majorIdValue,
                        credits: parseFloat(this.courseForm.credits), // 转换为浮点数，后端BigDecimal可以接受
                        totalHours: parseInt(this.courseForm.totalHours, 10),
                        theoryHours: parseInt(this.courseForm.theoryHours, 10),
                        practiceHours: parseInt(this.courseForm.practiceHours, 10),
                        courseType: this.courseForm.courseType,
                        courseNature: this.courseForm.courseNature,
                        courseStatus: this.courseForm.courseStatus || 'enabled' // 确保课程状态有有效值
                    };
                    
                    // 只有编辑时才包含courseId
                    if (this.courseForm.courseId !== null) {
                        courseData.courseId = this.courseForm.courseId;
                    }
                    
                    const isEdit = this.courseForm.courseId !== null;
                    const url = isEdit 
                        ? `${this.API_BASE_URL}/courses/${this.courseForm.courseId}` 
                        : `${this.API_BASE_URL}/courses`;
                    const method = isEdit ? 'PUT' : 'POST';
                    
                    console.log(`准备发送${isEdit ? '编辑' : '添加'}请求，URL: ${url}，数据:`, JSON.stringify(courseData)); // 用JSON.stringify确保看到实际发送的数据
                    fetch(url, {
                        method: method,
                        headers: {
                            'Content-Type': 'application/json',
                            'Accept': 'application/json'
                        },
                        credentials: 'include',
                        body: JSON.stringify(courseData)
                    })
                    .then(response => {
                        console.log('收到响应，状态:', response.status, '状态文本:', response.statusText);
                        if (!response.ok) {
                            // 保存响应信息，以便在catch中使用
                            return response.json().then(errorData => {
                                console.log('响应数据:', errorData);
                                throw {
                                    status: response.status,
                                    statusText: response.statusText,
                                    message: errorData.message || errorData.error || `HTTP error! status: ${response.status}`
                                };
                            }).catch(() => {
                                throw {
                                    status: response.status,
                                    statusText: response.statusText,
                                    message: `HTTP error! status: ${response.status}`
                                };
                            });
                        }
                        return response.json();
                    })
                    .then(data => {
                        console.log('请求成功，响应数据:', data);
                        this.$message.success(isEdit ? '课程更新成功' : '课程添加成功');
                        if (isEdit) {
                            this.editCourseDialogVisible = false;
                        } else {
                            this.addCourseDialogVisible = false;
                        }
                        this.queryCourses();
                    })
                    .catch(error => {
                        console.error('保存课程失败:', error);
                        // 尝试获取更具体的错误信息
                        let errorMessage = isEdit ? '课程更新失败' : '课程添加失败';
                        if (error.message) {
                            // 服务器返回了错误信息或网络错误
                            this.$message.error(`${errorMessage}: ${error.message}`);
                        } else {
                            this.$message.error(errorMessage);
                        }
                    });
                } else {
                    console.log('表单验证失败');
                }
            });
        },
        
        // 删除课程
        deleteCourse(courseId) {
            this.$confirm('确定要删除该课程吗？删除后相关章节和知识点也会被同步删除。', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'danger'
            }).then(() => {
                fetch(`${this.API_BASE_URL}/courses/${courseId}`, {
                    method: 'DELETE',
                    headers: {
                        'Accept': 'application/json'
                    },
                    credentials: 'include'
                })
                .then(response => {
                    console.log('删除课程响应:', response.status, response.statusText);
                    if (response.ok) {
                        return response.json().catch(() => ({})); // 处理非JSON响应
                    } else {
                        throw new Error('删除失败');
                    }
                })
                .then(data => {
                    if (data && data.success) {
                        this.$message.success(data.message || '课程删除成功');
                    } else {
                        this.$message.success('课程删除成功');
                    }
                    this.queryCourses();
                })
                .catch(error => {
                    console.error('删除课程失败:', error);
                    this.$message.error('课程删除失败');
                });
            }).catch(() => {
                this.$message.info('已取消删除');
            });
        },
        
        // 保存培养方案
        saveTrainingProgram() {
            this.$refs.trainingProgramForm.validate((valid) => {
                if (valid) {
                    const isEdit = this.trainingProgramForm.programId !== null;
                    const url = isEdit 
                        ? `${this.API_BASE_URL}/training-programs/${this.trainingProgramForm.programId}` 
                        : `${this.API_BASE_URL}/training-programs`;
                    const method = isEdit ? 'PUT' : 'POST';
                    
                    fetch(url, {
                        method: method,
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(this.trainingProgramForm)
                    })
                    .then(response => {
                        if (!response.ok) {
                            return response.json().then(errorData => {
                                throw { status: response.status, statusText: response.statusText, message: errorData.message || '操作失败' };
                            }).catch(() => {
                                throw { status: response.status, statusText: response.statusText, message: `HTTP错误! 状态: ${response.status}` };
                            });
                        }
                        return response.json();
                    })
                    .then(data => {
                        this.$message.success(isEdit ? '培养方案更新成功' : '培养方案添加成功');
                        if (isEdit) {
                            this.editTrainingProgramDialogVisible = false;
                        } else {
                            this.addTrainingProgramDialogVisible = false;
                        }
                        this.queryTrainingPrograms();
                    })
                    .catch(error => {
                        console.error('保存培养方案失败:', error);
                        this.$message.error(isEdit ? '培养方案更新失败' : '培养方案添加失败');
                    });
                }
            });
        },
        
        // 删除培养方案
        deleteTrainingProgram(programId) {
            this.$confirm('确定要删除该培养方案吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'danger'
            }).then(() => {
                fetch(`${this.API_BASE_URL}/training-programs/${programId}`, {
                    method: 'DELETE'
                })
                .then(response => {
                    if (response.ok) {
                        this.$message.success('培养方案删除成功');
                        this.queryTrainingPrograms();
                    } else {
                        throw new Error('删除失败');
                    }
                })
                .catch(error => {
                    console.error('删除培养方案失败:', error);
                    this.$message.error('培养方案删除失败');
                });
            }).catch(() => {
                this.$message.info('已取消删除');
            });
        },
        
        // 显示从URL导入培养方案对话框
        showImportFromUrlDialog() {
            this.importFromUrlForm = {
                url: 'https://cs.tiangong.edu.cn/1894/list.htm',
                majorId: '',
                batchId: ''
            };
            this.importFromUrlDialogVisible = true;
        },
        
        // 从URL导入培养方案
        importTrainingProgramFromUrl() {
            this.$refs.importFromUrlForm.validate((valid) => {
                if (valid) {
                    fetch(`${this.API_BASE_URL}/training-programs/import-from-url`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify({ url: this.importFromUrlForm.url })
                    })
                    .then(response => {
                        if (!response.ok) {
                            return response.json().then(errorData => {
                                throw {
                                    status: response.status,
                                    statusText: response.statusText,
                                    message: errorData.message || errorData.error || `HTTP error! status: ${response.status}`
                                };
                            }).catch(() => {
                                throw {
                                    status: response.status,
                                    statusText: response.statusText,
                                    message: `HTTP error! status: ${response.status}`
                                };
                            });
                        }
                        return response.json();
                    })
                    .then(data => {
                        this.$message.success('培养方案导入成功');
                        this.importFromUrlDialogVisible = false;
                        this.queryTrainingPrograms();
                    })
                    .catch(error => {
                        console.error('从URL导入培养方案失败:', error);
                        this.$message.error(`培养方案导入失败: ${error.message || '未知错误'}`);
                    });
                }
            });
        },
        
        // 删除题目
        deleteQuestion(questionId) {
            this.$confirm('确定要删除该题目吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'danger'
            }).then(() => {
                fetch(`${this.API_BASE_URL}/questions/${questionId}`, {
                    method: 'DELETE'
                })
                .then(response => {
                    if (response.ok) {
                        this.$message.success('题目删除成功');
                        this.queryQuestions();
                    } else {
                        throw new Error('删除失败');
                    }
                })
                .catch(error => {
                    console.error('删除题目失败:', error);
                    this.$message.error('题目删除失败');
                });
            }).catch(() => {
                this.$message.info('已取消删除');
            });
        },
        
        // 查询题目
        queryQuestions() {
            console.log('调用queryQuestions方法');
            console.log('筛选条件:', this.questionBankFilters);
            
            // 构建查询URL，包含筛选条件
            let url = `${this.API_BASE_URL}/questions?`;
            
            // 添加筛选条件
            if (this.questionBankFilters.courseId) url += `courseId=${this.questionBankFilters.courseId}&`;
            if (this.questionBankFilters.chapterId) url += `chapterId=${this.questionBankFilters.chapterId}&`;
            if (this.questionBankFilters.questionType) url += `questionType=${this.questionBankFilters.questionType}&`;
            
            // 移除末尾的&或?
            url = url.replace(/[&?]$/, '');
            
            console.log('请求URL:', url);
            
            this.apiFetch(url)
            .then(response => {
                console.log('响应状态:', response.status);
                return response.json();
            })
            .then(data => {
                console.log('响应数据:', data);
                if (data && data.success) {
                    // 处理 { success: true, data: [...] } 格式
                    this.questionsList = data.data;
                    console.log('题目列表:', this.questionsList);
                } else if (Array.isArray(data)) {
                    // 兼容直接返回数组的格式
                    this.questionsList = data;
                    console.log('题目列表:', this.questionsList);
                } else {
                    this.questionsList = [];
                    console.error('获取题目列表失败，响应数据格式不正确:', data);
                    this.$message.error('获取题目列表失败');
                }
            })
            .catch(error => {
                console.error('获取题目列表失败:', error);
                this.questionsList = [];
                this.$message.error('获取题目列表失败');
            });
        }
    }
});